package com.smartsubstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartsubstation.common.exception.BusinessException;
import com.smartsubstation.common.result.ResultCode;
import com.smartsubstation.common.util.JwtUtil;
import com.smartsubstation.dto.LoginRequest;
import com.smartsubstation.dto.LoginResponse;
import com.smartsubstation.entity.SysUser;
import com.smartsubstation.mapper.SysUserMapper;
import com.smartsubstation.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String USER_TOKEN_PREFIX = "user:token:";
    private static final int TOKEN_EXPIRE_HOURS = 2;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, request.getUsername())
        );

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 检查状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_ACCOUNT_DISABLED);
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getUserId(), user.getRoleId());

        // 存储Token到Redis
        String tokenKey = USER_TOKEN_PREFIX + user.getUserId();
        redisTemplate.opsForValue().set(tokenKey, token, TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户登录成功: userId={}, username={}", user.getUserId(), user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roleId(user.getRoleId())
                .avatar(user.getAvatar())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(String token) {
        if (!StringUtils.hasText(token)) {
            return;
        }

        try {
            String username = jwtUtil.extractUsername(token);
            Long userId = jwtUtil.extractUserId(token);

            // 将Token加入黑名单
            String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(blacklistKey, "1", TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);

            // 删除用户Token
            String tokenKey = USER_TOKEN_PREFIX + userId;
            redisTemplate.delete(tokenKey);

            log.info("用户登出: userId={}, username={}", userId, username);
        } catch (Exception e) {
            log.warn("登出处理失败: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String refreshToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        String username = jwtUtil.extractUsername(token);

        // 检查Token是否在黑名单中
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            throw new BusinessException(ResultCode.TOKEN_INVALID);
        }

        // 刷新Token
        String newToken = jwtUtil.refreshToken(token);

        // 更新Redis中的Token
        Long userId = jwtUtil.extractUserId(token);
        String tokenKey = USER_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(tokenKey, newToken, TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);

        // 将旧Token加入黑名单
        redisTemplate.opsForValue().set(blacklistKey, "1", TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);

        log.info("Token刷新成功: userId={}, username={}", userId, username);

        return newToken;
    }
}

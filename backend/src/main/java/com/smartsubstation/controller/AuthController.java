package com.smartsubstation.controller;

import com.smartsubstation.common.result.Result;
import com.smartsubstation.dto.LoginRequest;
import com.smartsubstation.dto.LoginResponse;
import com.smartsubstation.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户登录、登出、Token刷新")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    private static final String HEADER_PREFIX = "Bearer ";

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        authService.logout(token);
        return Result.success();
    }

    /**
     * 刷新Token
     */
    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<String> refreshToken(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        String newToken = authService.refreshToken(token);
        return Result.success(newToken);
    }

    /**
     * 从Authorization头中提取Token
     */
    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(HEADER_PREFIX)) {
            return null;
        }
        return authorization.substring(HEADER_PREFIX.length());
    }
}

package com.smartsubstation.service;

import com.smartsubstation.dto.LoginRequest;
import com.smartsubstation.dto.LoginResponse;

/**
 * 认证服务接口
 */
public interface IAuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     */
    void logout(String token);

    /**
     * 刷新Token
     *
     * @param token 旧Token
     * @return 新Token
     */
    String refreshToken(String token);
}

package com.market.controller;

import com.market.dto.AuthResponse;
import com.market.dto.LoginRequest;
import com.market.dto.RegisterRequest;
import com.market.dto.MerchantRegisterRequest;
import com.market.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 * 提供用户注册、登录、商家注册等认证接口。
 * 权限要求：公开接口，无需登录
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册
     * API路径：POST /api/auth/register
     * 权限：公开
     *
     * @param request 注册请求（包含用户名、密码、邮箱等）
     * @return 认证响应（包含 JWT Token 和用户信息）
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 商家注册
     * API路径：POST /api/auth/merchant-register
     * 权限：公开
     *
     * @param request 商家注册请求（包含商家信息、店铺信息等）
     * @return 认证响应（包含 JWT Token 和商家信息）
     */
    @PostMapping("/merchant-register")
    public ResponseEntity<AuthResponse> registerMerchant(@Valid @RequestBody MerchantRegisterRequest request) {
        AuthResponse response = userService.registerMerchant(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 用户登录
     * API路径：POST /api/auth/login
     * 权限：公开
     *
     * @param request 登录请求（包含用户名/邮箱/手机号和密码）
     * @return JWT Token 和用户基本信息
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前用户信息
     * API路径：GET /api/auth/me
     * 权限：需要登录
     *
     * @return 当前用户信息或未登录提示
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(AuthResponse.failure("请先登录"));
    }
}

package com.market.controller;

import com.market.dto.AuthResponse;
import com.market.dto.LoginRequest;
import com.market.dto.RegisterRequest;
import com.market.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 * <p>
 * 处理用户注册、登录和当前用户信息查询请求。
 * 所有接口允许跨域访问（@CrossOrigin(origins = "*")）。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    /**
     * 用户服务
     */
    private final UserService userService;

    /**
     * 构造函数
     *
     * @param userService 用户服务
     */
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口
     * <p>
     * POST /api/auth/register
     * 接收用户注册请求，验证用户信息，创建新用户账户。
     * </p>
     *
     * @param request 注册请求数据，包含用户名、邮箱、密码、确认密码和验证码
     * @return 注册结果，包含JWT token和用户信息
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 用户登录接口
     * <p>
     * POST /api/auth/login
     * 接收用户登录请求，验证用户名和密码，生成JWT token。
     * </p>
     *
     * @param request 登录请求数据，包含用户名和密码
     * @return 登录结果，包含JWT token和用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前用户信息接口
     * <p>
     * GET /api/auth/me
     * 获取当前登录用户的详细信息。
     * 注意：此接口需要JWT认证，当前实现待完善。
     * </p>
     *
     * @return 当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // 需要添加JWT认证
        return ResponseEntity.ok(AuthResponse.failure("请先登录"));
    }
}
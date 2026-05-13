package com.market.interceptor;

import com.market.entity.User;
import com.market.repository.UserRepository;
import com.market.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 认证拦截器
 * 拦截 HTTP 请求，从 Authorization 请求头中提取 JWT Token，
 * 验证 Token 有效性后将用户信息设置到 Spring Security 上下文中
 *
 * @author market-team
 * @since 1.0
 */
@Component
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    /**
     * 在请求处理前执行，提取并验证 JWT Token
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  请求处理器
     * @return 是否继续处理请求（始终返回 true）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            try {
                String username = jwtService.extractUsername(token);

                if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    User user = userRepository.findByName(username).orElse(null);

                    if (user != null && jwtService.isTokenValid(token, user)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                // Token 无效，继续处理请求（由需要认证的接口自行判断）
            }
        }

        return true;
    }

    /**
     * 从 HTTP 请求的 Authorization 头中提取 JWT Token
     *
     * @param request HTTP 请求
     * @return JWT Token 字符串，如果不存在则返回 null
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

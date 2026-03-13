package com.market.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * <p>
 * 继承OncePerRequestFilter，确保每个请求只执行一次过滤。
 * 负责从请求头中提取JWT令牌，验证令牌有效性，并设置用户认证信息。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT服务
     */
    private final JwtService jwtService;

    /**
     * 用户详情服务
     * 使用@Lazy注解避免循环依赖
     */
    private final UserDetailsService userDetailsService;

    /**
     * 构造函数
     *
     * @param jwtService JWT服务
     * @param userDetailsService 用户详情服务
     */
    public JwtAuthenticationFilter(JwtService jwtService, @Lazy UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 执行JWT认证过滤
     * <p>
     * 过滤流程：
     * <ol>
     *   <li>从请求头中提取Authorization字段</li>
     *   <li>验证是否为Bearer令牌</li>
     *   <li>从令牌中提取用户名</li>
     *   <li>验证令牌有效性</li>
     *   <li>如果有效，设置认证信息到SecurityContext</li>
     * </ol>
     * </p>
     *
     * @param request HTTP请求
     * @param response HTTP响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
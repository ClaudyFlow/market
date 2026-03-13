package com.market.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT（JSON Web Token）服务类
 * <p>
 * 负责JWT令牌的生成、验证和解析。
 * 提供以下功能：
 * <ul>
 *   <li>生成用户认证令牌</li>
 *   <li>从令牌中提取用户信息</li>
 *   <li>验证令牌的有效性</li>
 *   <li>检查令牌是否过期</li>
 * </ul>
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@Service
public class JwtService {

    /**
     * JWT签名密钥
     * 从配置文件中读取，用于令牌的签名和验证
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT令牌有效期（毫秒）
     * 从配置文件中读取，默认为24小时
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * 从令牌中提取用户名
     *
     * @param token JWT令牌
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从令牌中提取指定的声明
     *
     * @param token JWT令牌
     * @param claimsResolver 声明解析函数
     * @param <T> 声明值的类型
     * @return 声明值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 生成用户认证令牌
     *
     * @param userDetails 用户详情
     * @return JWT令牌字符串
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * 生成包含额外声明的用户认证令牌
     *
     * @param extraClaims 额外的声明信息
     * @param userDetails 用户详情
     * @return JWT令牌字符串
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * 获取令牌有效期
     *
     * @return 令牌有效期（毫秒）
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }

    /**
     * 构建JWT令牌
     *
     * @param extraClaims 额外的声明信息
     * @param userDetails 用户详情
     * @param expiration 令牌有效期（毫秒）
     * @return JWT令牌字符串
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * 验证令牌是否有效
     *
     * @param token JWT令牌
     * @param userDetails 用户详情
     * @return 如果令牌有效返回true，否则返回false
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * 检查令牌是否过期
     *
     * @param token JWT令牌
     * @return 如果令牌已过期返回true，否则返回false
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 从令牌中提取过期时间
     *
     * @param token JWT令牌
     * @return 过期时间
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从令牌中提取所有声明
     *
     * @param token JWT令牌
     * @return 声明集合
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取签名密钥
     * <p>
     * 将配置的密钥字符串转换为HMAC-SHA算法使用的SecretKey对象。
     * </p>
     *
     * @return 签名密钥
     */
    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
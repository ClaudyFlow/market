package com.market.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * 用户实体类
 * <p>
 * 此类代表市场平台中的用户信息，实现了Spring Security的UserDetails接口，
 * 用于支持基于JWT的用户认证和授权。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {
    /**
     * 用户ID
     * 主键，自动生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     * 唯一标识，长度限制为50个字符
     */
    @Column(unique = true, nullable = false, length = 50)
    private String name;

    /**
     * 用户邮箱
     * 唯一标识，用于登录和接收验证邮件，长度限制为100个字符
     */
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    /**
     * 密码哈希值
     * 使用BCrypt算法加密后的密码，长度限制为255个字符
     */
    @Column(nullable = false, length = 255)
    private String passwordHash;

    /**
     * 头像URL
     * 用户头像的访问地址，长度限制为255个字符
     */
    @Column(length = 255)
    private String avatarUrl;

    /**
     * 当前积分
     * 用户当前可用的积分余额，默认值为0
     */
    @Column(name = "points", nullable = false)
    private Integer points = 0;

    /**
     * 累计积分
     * 用户历史上获得的所有积分总和（包括已使用的积分），默认值为0
     */
    @Column(name = "total_points", nullable = false)
    private Integer totalPoints = 0;

    /**
     * 创建时间
     * 用户注册的时间戳，创建后不可修改
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 用户信息最后更新的时间戳
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 无参构造函数
     * 供JPA框架使用
     */
    public User() {
    }

    /**
     * 带参构造函数
     * 用于创建新用户
     *
     * @param name 用户名
     * @param email 邮箱
     * @param passwordHash 密码哈希值
     */
    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    /**
     * 创建前生命周期回调
     * 在实体首次持久化之前自动设置创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * 更新前生命周期回调
     * 在实体更新之前自动设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户ID
     *
     * @param id 用户ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户名
     *
     * @param name 用户名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取用户邮箱
     *
     * @return 用户邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置用户邮箱
     *
     * @param email 用户邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取密码哈希值
     *
     * @return 密码哈希值
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * 设置密码哈希值
     *
     * @param passwordHash 密码哈希值
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * 获取头像URL
     *
     * @return 头像URL
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * 设置头像URL
     *
     * @param avatarUrl 头像URL
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * 获取当前积分
     *
     * @return 当前积分
     */
    public Integer getPoints() {
        return points;
    }

    /**
     * 设置当前积分
     *
     * @param points 当前积分
     */
    public void setPoints(Integer points) {
        this.points = points;
    }

    /**
     * 获取累计积分
     *
     * @return 累计积分
     */
    public Integer getTotalPoints() {
        return totalPoints;
    }

    /**
     * 设置累计积分
     *
     * @param totalPoints 累计积分
     */
    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置更新时间
     *
     * @param updatedAt 更新时间
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取用户权限集合
     * 当前实现返回空集合，所有用户权限相同
     *
     * @return 权限集合（当前为空）
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * 获取密码
     * Spring Security认证时使用，返回密码哈希值
     *
     * @return 密码哈希值
     */
    @Override
    public String getPassword() {
        return passwordHash;
    }

    /**
     * 获取用户名
     * Spring Security认证时使用，返回用户名作为认证主体
     *
     * @return 用户名
     */
    @Override
    public String getUsername() {
        return name;
    }

    /**
     * 检查账户是否未过期
     * 当前实现始终返回true
     *
     * @return true（账户未过期）
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 检查账户是否未锁定
     * 当前实现始终返回true
     *
     * @return true（账户未锁定）
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 检查凭据是否未过期
     * 当前实现始终返回true
     *
     * @return true（凭据未过期）
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 检查账户是否启用
     * 当前实现始终返回true
     *
     * @return true（账户已启用）
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
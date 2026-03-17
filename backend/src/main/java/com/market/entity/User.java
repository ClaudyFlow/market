package com.market.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * 用户实体类
 */
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @Column(unique = true, nullable = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 255)
    private String avatarUrl;

    @Column(name = "credit", nullable = false)
    private Integer credit = 0;

    @Column(name = "total_credit", nullable = false)
    private Integer totalCredit = 0;

    @Column(name = "consumed_credit", nullable = false)
    private Integer consumedCredit = 0;

    @Column(name = "vip_level", nullable = false)
    private Integer vipLevel = 0;

    @Column(name = "vip_expire_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date vipExpireTime;

    @Column(name = "growth_value", nullable = false)
    private Integer growthValue = 0;

    @Column(name = "consecutive_checkin_days", nullable = false)
    private Integer consecutiveCheckinDays = 0;

    @Column(name = "last_checkin_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastCheckInTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {}

    public User(String name, String email, String passwordHash) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 基础字段
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public Integer getCredit() { return credit; }
    public void setCredit(Integer credit) { this.credit = credit; }

    public Integer getTotalCredit() { return totalCredit; }
    public void setTotalCredit(Integer totalCredit) { this.totalCredit = totalCredit; }

    // VIP 相关字段
    public Integer getVipLevel() { return vipLevel; }
    public void setVipLevel(Integer vipLevel) { this.vipLevel = vipLevel; }

    public Date getVipExpireTime() { return vipExpireTime; }
    public void setVipExpireTime(Date vipExpireTime) { this.vipExpireTime = vipExpireTime; }

    public Integer getGrowthValue() { return growthValue; }
    public void setGrowthValue(Integer growthValue) { this.growthValue = growthValue; }

    // 签到相关字段
    public Integer getConsecutiveCheckinDays() { return consecutiveCheckinDays; }
    public void setConsecutiveCheckinDays(Integer consecutiveCheckinDays) { this.consecutiveCheckinDays = consecutiveCheckinDays; }

    public Date getLastCheckInTime() { return lastCheckInTime; }
    public void setLastCheckInTime(Date lastCheckInTime) { this.lastCheckInTime = lastCheckInTime; }

    // 积分消费字段
    public Integer getConsumedCredit() { return consumedCredit; }
    public void setConsumedCredit(Integer consumedCredit) { this.consumedCredit = consumedCredit; }

    // 时间字段
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Spring Security 接口实现
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}

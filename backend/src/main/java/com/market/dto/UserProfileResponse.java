package com.market.dto;

import java.time.LocalDateTime;

/**
 * 用户信息响应数据传输对象
 * <p>
 * 用于返回用户的完整信息。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
public class UserProfileResponse {

    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像 URL
     */
    private String avatarUrl;

    /**
     * 用户简介
     */
    private String bio;

    /**
     * 积分
     */
    private Integer credit;

    /**
     * 总积分
     */
    private Integer totalCredit;

    /**
     * VIP 等级
     */
    private Integer vipLevel;

    /**
     * 成长值
     */
    private Integer growthValue;

    /**
     * 连续签到天数
     */
    private Integer consecutiveCheckinDays;

    /**
     * 角色
     */
    private String role;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;

    /**
     * 关注数
     */
    private Long followingCount;

    /**
     * 粉丝数
     */
    private Long followerCount;

    /**
     * 收藏数
     */
    private Long favoriteCount;

    /**
     * 是否已关注（当前登录用户是否关注了该用户）
     */
    private Boolean isFollowing;

    public UserProfileResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(Integer totalCredit) {
        this.totalCredit = totalCredit;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

    public Integer getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(Integer growthValue) {
        this.growthValue = growthValue;
    }

    public Integer getConsecutiveCheckinDays() {
        return consecutiveCheckinDays;
    }

    public void setConsecutiveCheckinDays(Integer consecutiveCheckinDays) {
        this.consecutiveCheckinDays = consecutiveCheckinDays;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Long followingCount) {
        this.followingCount = followingCount;
    }

    public Long getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Long followerCount) {
        this.followerCount = followerCount;
    }

    public Long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Boolean getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }
}

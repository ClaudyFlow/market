package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 积分历史记录实体类
 * <p>
 * 此类记录用户的所有积分变化历史，包括积分增加和扣除操作。
 * 每次积分变化都会创建一条记录，用于追踪积分来源和用途。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@Entity
@Table(name = "credit_history")
public class CreditHistory {

    /**
     * 记录ID
     * 主键，自动生成
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     * 关联的用户ID，用于标识是哪个用户的积分变化
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 积分变化量
     * 正数表示增加积分，负数表示扣除积分
     */
    @Column(name = "credit_change", nullable = false)
    private Integer creditChange; // 正数为增加，负数为减少

    /**
     * 变化后的余额
     * 积分变化后用户的积分余额
     */
    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter; // 变化后的余额

    /**
     * 积分变化原因
     * 说明积分变化的来源或用途，如：购物获得、兑换商品、签到奖励等
     */
    @Column(name = "reason", nullable = false, length = 100)
    private String reason; // 积分变化原因：购物获得、兑换商品、签到奖励等

    /**
     * 关联的订单ID
     * 如果积分变化与某个订单相关，可以记录订单ID以便追踪
     */
    @Column(name = "related_order_id", length = 50)
    private String relatedOrderId; // 关联的订单ID

    /**
     * 创建时间
     * 积分变化发生的时间戳，创建后不可修改
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 无参构造函数
     * 供JPA框架使用
     */
    public CreditHistory() {
    }

    /**
     * 带参构造函数
     * 用于创建积分历史记录
     *
     * @param userId 用户ID
     * @param creditChange 积分变化量（正数增加，负数减少）
     * @param balanceAfter 变化后的余额
     * @param reason 积分变化原因
     */
    public CreditHistory(Long userId, Integer creditChange, Integer balanceAfter, String reason) {
        this.userId = userId;
        this.creditChange = creditChange;
        this.balanceAfter = balanceAfter;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 创建前生命周期回调
     * 在实体首次持久化之前自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * 获取记录ID
     *
     * @return 记录ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置记录ID
     *
     * @param id 记录ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取积分变化量
     *
     * @return 积分变化量（正数增加，负数减少）
     */
    public Integer getCreditChange() {
        return creditChange;
    }

    /**
     * 设置积分变化量
     *
     * @param creditChange 积分变化量（正数增加，负数减少）
     */
    public void setCreditChange(Integer creditChange) {
        this.creditChange = creditChange;
    }

    /**
     * 获取变化后的余额
     *
     * @return 变化后的余额
     */
    public Integer getBalanceAfter() {
        return balanceAfter;
    }

    /**
     * 设置变化后的余额
     *
     * @param balanceAfter 变化后的余额
     */
    public void setBalanceAfter(Integer balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    /**
     * 获取积分变化原因
     *
     * @return 积分变化原因
     */
    public String getReason() {
        return reason;
    }

    /**
     * 设置积分变化原因
     *
     * @param reason 积分变化原因
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * 获取关联的订单ID
     *
     * @return 关联的订单ID
     */
    public String getRelatedOrderId() {
        return relatedOrderId;
    }

    /**
     * 设置关联的订单ID
     *
     * @param relatedOrderId 关联的订单ID
     */
    public void setRelatedOrderId(String relatedOrderId) {
        this.relatedOrderId = relatedOrderId;
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
}
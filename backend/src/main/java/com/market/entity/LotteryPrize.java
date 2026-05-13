package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 抽奖奖品实体类
 * 对应数据库表：lottery_prize
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "lottery_prize")
public class LotteryPrize {

    /**
     * 奖品唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 奖品名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 奖品描述
     */
    @Column(length = 255)
    private String description;

    /**
     * 奖品类型（1=积分，2=实物）
     */
    @Column(nullable = false)
    private Integer type;

    /**
     * 抽奖权重
     */
    @Column(nullable = false)
    private Integer weight;

    /**
     * 奖品图片URL
     */
    @Column(length = 255)
    private String image;

    /**
     * 是否可用
     */
    @Column(nullable = false)
    private Boolean available = true;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public LotteryPrize() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }

    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

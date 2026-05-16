package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商品分类实体类
 * 对应数据库表：category
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 分类名称（如：数码产品、日用品、服装）
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 分类图标
     */
    @Column(length = 255)
    private String icon;

    /**
     * 分类路径/路由
     */
    @Column(length = 100)
    private String path;

    /**
     * 父分类ID（顶级分类为null）
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 排序顺序
     */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /**
     * 状态（ACTIVE正常、DISABLED禁用）
     */
    @Column(length = 20)
    private String status = "ACTIVE";

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Category() {}

    public Category(String name, String path) {
        this.name = name;
        this.path = path;
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

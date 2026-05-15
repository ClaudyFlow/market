package com.market.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 敏感词实体类
 * 对应数据库表：sensitive_word
 */
@Entity
@Table(name = "sensitive_word")
public class SensitiveWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 敏感词内容
     */
    @Column(nullable = false, unique = true, length = 100)
    private String word;

    /**
     * 敏感词类型 (POLITICAL-政治, PORN-色情, VIOLENCE-暴力, ABUSE-辱骂, SPAM-广告, CUSTOM-自定义)
     */
    @Column(nullable = false, length = 50)
    private String type = "CUSTOM";

    /**
     * 敏感级别 (LOW-低, MEDIUM-中, HIGH-高)
     */
    @Column(nullable = false, length = 20)
    private String level = "MEDIUM";

    /**
     * 是否启用
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 替换词（用于自动替换敏感词）
     */
    @Column(length = 50)
    private String replacement = "***";

    /**
     * 匹配次数（用于统计高频敏感词）
     */
    @Column(nullable = false)
    private Integer matchCount = 0;

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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public SensitiveWord() {}

    public SensitiveWord(String word, String type, String level) {
        this.word = word;
        this.type = type;
        this.level = level;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getReplacement() { return replacement; }
    public void setReplacement(String replacement) { this.replacement = replacement; }

    public Integer getMatchCount() { return matchCount; }
    public void setMatchCount(Integer matchCount) { this.matchCount = matchCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * 增加匹配次数
     */
    public void incrementMatchCount() {
        this.matchCount++;
    }
}

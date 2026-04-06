package com.market.entity;

import jakarta.persistence.Embeddable;
import java.util.Date;
import java.util.List;

/**
 * VIP信息实体类（嵌入式）
 *
 * @author market-team
 * @since 1.0
 */
@Embeddable
public class VipInfo {

    /**
     * VIP等级
     */
    private Integer level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 权益列表
     */
    private List<String> benefits;

    /**
     * 当前成长值
     */
    private Integer growthValue;

    /**
     * 升级到下一级所需成长值
     */
    private Integer nextLevelGrowth;

    public VipInfo() {}

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }

    public Date getExpireTime() { return expireTime; }
    public void setExpireTime(Date expireTime) { this.expireTime = expireTime; }

    public List<String> getBenefits() { return benefits; }
    public void setBenefits(List<String> benefits) { this.benefits = benefits; }

    public Integer getGrowthValue() { return growthValue; }
    public void setGrowthValue(Integer growthValue) { this.growthValue = growthValue; }

    public Integer getNextLevelGrowth() { return nextLevelGrowth; }
    public void setNextLevelGrowth(Integer nextLevelGrowth) { this.nextLevelGrowth = nextLevelGrowth; }
}

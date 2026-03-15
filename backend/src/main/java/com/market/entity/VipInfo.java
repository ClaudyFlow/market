package com.market.entity;

import java.util.Date;
import java.util.List;

/**
 * VIP 信息实体
 */
public class VipInfo {

    private Integer level;
    private String levelName;
    private Date expireTime;
    private List<String> benefits;
    private Integer growthValue;
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

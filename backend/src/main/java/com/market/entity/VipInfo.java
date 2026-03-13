package com.market.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * VIP 信息实体
 */
@Data
public class VipInfo {
    
    /**
     * VIP 等级 (0-5)
     * 0: 普通会员
     * 1: 白银会员
     * 2: 黄金会员
     * 3: 铂金会员
     * 4: 钻石会员
     * 5: 至尊会员
     */
    private Integer level;
    
    /**
     * VIP 等级名称
     */
    private String levelName;
    
    /**
     * 过期时间
     */
    private Date expireTime;
    
    /**
     * VIP 权益列表
     */
    private List<String> benefits;
    
    /**
     * 成长值
     */
    private Integer growthValue;
    
    /**
     * 下一等级所需成长值
     */
    private Integer nextLevelGrowth;
}

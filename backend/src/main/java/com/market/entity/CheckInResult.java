package com.market.entity;

import lombok.Data;

/**
 * 签到结果
 */
@Data
public class CheckInResult {
    
    /**
     * 是否签到成功
     */
    private Boolean success;
    
    /**
     * 获得积分
     */
    private Integer points;
    
    /**
     * 是否已签到
     */
    private Boolean hasCheckedIn;
    
    /**
     * 连续签到天数
     */
    private Integer consecutiveDays;
    
    /**
     * 消息
     */
    private String message;
}

package com.market.entity;

import lombok.Data;
import java.util.Date;

/**
 * 用户积分信息
 */
@Data
public class UserPointsInfo {
    
    /**
     * 当前积分
     */
    private Integer points;
    
    /**
     * 累计获得积分
     */
    private Integer totalPoints;
    
    /**
     * 累计消费积分
     */
    private Integer consumedPoints;
    
    /**
     * 今日是否已签到
     */
    private Boolean hasCheckedIn;
    
    /**
     * 连续签到天数
     */
    private Integer consecutiveDays;
    
    /**
     * 最后签到时间
     */
    private Date lastCheckInTime;
}

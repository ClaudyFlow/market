package com.market.entity;

import java.util.Date;

/**
 * 用户积分信息
 */
public class UserPointsInfo {

    private Integer points;
    private Integer totalPoints;
    private Integer consumedPoints;
    private Boolean hasCheckedIn;
    private Integer consecutiveDays;
    private Date lastCheckInTime;

    public UserPointsInfo() {}

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Integer getTotalPoints() { return totalPoints; }
    public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }

    public Integer getConsumedPoints() { return consumedPoints; }
    public void setConsumedPoints(Integer consumedPoints) { this.consumedPoints = consumedPoints; }

    public Boolean getHasCheckedIn() { return hasCheckedIn; }
    public void setHasCheckedIn(Boolean hasCheckedIn) { this.hasCheckedIn = hasCheckedIn; }

    public Integer getConsecutiveDays() { return consecutiveDays; }
    public void setConsecutiveDays(Integer consecutiveDays) { this.consecutiveDays = consecutiveDays; }

    public Date getLastCheckInTime() { return lastCheckInTime; }
    public void setLastCheckInTime(Date lastCheckInTime) { this.lastCheckInTime = lastCheckInTime; }
}

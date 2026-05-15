package com.market.entity;

import java.util.Date;

/**
 * 用户积分信息
 */
public class UserCreditInfo {

    private Integer credit;
    private Integer totalCredit;
    private Integer consumedCredit;
    private Boolean hasCheckedIn;
    private Integer consecutiveDays;
    private Date lastCheckInTime;

    public UserCreditInfo() {}

    public Integer getCredit() { return credit; }
    public void setCredit(Integer credit) { this.credit = credit; }

    public Integer getTotalCredit() { return totalCredit; }
    public void setTotalCredit(Integer totalCredit) { this.totalCredit = totalCredit; }

    public Integer getConsumedCredit() { return consumedCredit; }
    public void setConsumedCredit(Integer consumedCredit) { this.consumedCredit = consumedCredit; }

    public Boolean getHasCheckedIn() { return hasCheckedIn; }
    public void setHasCheckedIn(Boolean hasCheckedIn) { this.hasCheckedIn = hasCheckedIn; }

    public Integer getConsecutiveDays() { return consecutiveDays; }
    public void setConsecutiveDays(Integer consecutiveDays) { this.consecutiveDays = consecutiveDays; }

    public Date getLastCheckInTime() { return lastCheckInTime; }
    public void setLastCheckInTime(Date lastCheckInTime) { this.lastCheckInTime = lastCheckInTime; }
}
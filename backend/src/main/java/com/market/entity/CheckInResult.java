package com.market.entity;

/**
 * 签到结果
 */
public class CheckInResult {

    private Boolean success;
    private Integer credit;
    private Boolean hasCheckedIn;
    private Integer consecutiveDays;
    private String message;

    public CheckInResult() {}

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public Integer getCredit() { return credit; }
    public void setCredit(Integer credit) { this.credit = credit; }

    public Boolean getHasCheckedIn() { return hasCheckedIn; }
    public void setHasCheckedIn(Boolean hasCheckedIn) { this.hasCheckedIn = hasCheckedIn; }

    public Integer getConsecutiveDays() { return consecutiveDays; }
    public void setConsecutiveDays(Integer consecutiveDays) { this.consecutiveDays = consecutiveDays; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

package com.market.service;

import com.market.entity.User;

/**
 * 抽奖服务接口
 */
public interface LotteryService {

    /**
     * 抽奖
     * @param user 用户
     * @return 抽奖结果
     */
    LotteryResult draw(User user);

    /**
     * 获取用户的抽奖记录
     * @param userId 用户 ID
     * @return 抽奖记录列表
     */
    java.util.List<LotteryRecordDto> getRecords(Long userId);

    /**
     * 获取奖品列表
     * @return 奖品列表
     */
    java.util.List<LotteryPrizeDto> getPrizes();

    /**
     * 抽奖结果 DTO
     */
    class LotteryResult {
        private boolean success;
        private String message;
        private Long prizeId;
        private String prizeName;
        private Integer prizeType;
        private Integer cost;
        private Integer remainingCredit;

        public LotteryResult() {}

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Long getPrizeId() { return prizeId; }
        public void setPrizeId(Long prizeId) { this.prizeId = prizeId; }
        public String getPrizeName() { return prizeName; }
        public void setPrizeName(String prizeName) { this.prizeName = prizeName; }
        public Integer getPrizeType() { return prizeType; }
        public void setPrizeType(Integer prizeType) { this.prizeType = prizeType; }
        public Integer getCost() { return cost; }
        public void setCost(Integer cost) { this.cost = cost; }
        public Integer getRemainingCredit() { return remainingCredit; }
        public void setRemainingCredit(Integer remainingCredit) { this.remainingCredit = remainingCredit; }
    }

    /**
     * 抽奖记录 DTO
     */
    class LotteryRecordDto {
        private Long id;
        private String prizeName;
        private Integer prizeType;
        private Integer cost;
        private java.time.LocalDateTime createdAt;

        public LotteryRecordDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getPrizeName() { return prizeName; }
        public void setPrizeName(String prizeName) { this.prizeName = prizeName; }
        public Integer getPrizeType() { return prizeType; }
        public void setPrizeType(Integer prizeType) { this.prizeType = prizeType; }
        public Integer getCost() { return cost; }
        public void setCost(Integer cost) { this.cost = cost; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    /**
     * 奖品 DTO
     */
    class LotteryPrizeDto {
        private Long id;
        private String name;
        private String description;
        private Integer type;
        private String image;

        public LotteryPrizeDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Integer getType() { return type; }
        public void setType(Integer type) { this.type = type; }
        public String getImage() { return image; }
        public void setImage(String image) { this.image = image; }
    }
}

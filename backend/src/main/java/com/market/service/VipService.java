package com.market.service;

import com.market.entity.*;
import com.market.repository.*;
import com.market.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * VIP 服务类
 */
@Service
public class VipService {

    @Autowired
    private VipLevelRepository vipLevelRepository;

    @Autowired
    private VipGiftRepository vipGiftRepository;

    @Autowired
    private VipGiftRecordRepository vipGiftRecordRepository;

    @Autowired
    private VipRechargeOrderRepository vipRechargeOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditService creditService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserNotificationService notificationService;

    // ==================== VIP 等级管理 ====================

    /**
     * 获取所有 VIP 等级
     */
    public List<VipLevel> getAllVipLevels() {
        return vipLevelRepository.findAllByOrderByLevelAsc();
    }

    /**
     * 获取 VIP 等级详情
     */
    public VipLevel getVipLevel(Integer level) {
        return vipLevelRepository.findByLevel(level)
            .orElseThrow(() -> new RuntimeException("VIP 等级不存在"));
    }

    /**
     * 根据成长值获取当前等级
     */
    public VipLevel getCurrentLevel(Integer growthValue) {
        return vipLevelRepository.findHighestLevelByGrowthValue(growthValue)
            .orElse(getVipLevel(0));
    }

    /**
     * 获取下一个等级
     */
    public VipLevel getNextLevel(Integer currentLevel) {
        return vipLevelRepository.findByLevel(currentLevel + 1).orElse(null);
    }

    /**
     * 计算等级进度
     */
    public Map<String, Object> getLevelProgress(User user) {
        Map<String, Object> progress = new HashMap<>();
        
        int growthValue = user.getGrowthValue();
        VipLevel currentLevel = getCurrentLevel(growthValue);
        VipLevel nextLevel = getNextLevel(currentLevel.getLevel());
        
        progress.put("currentLevel", currentLevel);
        progress.put("growthValue", growthValue);
        
        if (nextLevel != null) {
            int required = nextLevel.getGrowthValueRequired();
            int prevRequired = currentLevel.getGrowthValueRequired();
            int progressPercent = (required - prevRequired) > 0
                ? (int) ((growthValue - prevRequired) * 100 / (required - prevRequired))
                : 100;
            progress.put("nextLevel", nextLevel);
            progress.put("progressPercent", Math.min(100, progressPercent));
            progress.put("remainingGrowth", required - growthValue);
        } else {
            progress.put("nextLevel", null);
            progress.put("progressPercent", 100);
            progress.put("remainingGrowth", 0);
        }
        
        return progress;
    }

    // ==================== VIP 礼包管理 ====================

    /**
     * 获取所有可用礼包
     */
    public List<VipGift> getAvailableGifts(Integer vipLevel) {
        return vipGiftRepository.findPeriodGifts(vipLevel);
    }

    /**
     * 获取每日礼包
     */
    public List<VipGift> getDailyGifts(Integer vipLevel) {
        return vipGiftRepository.findAvailableGifts("DAILY", vipLevel);
    }

    /**
     * 获取每月礼包
     */
    public List<VipGift> getMonthlyGifts(Integer vipLevel) {
        return vipGiftRepository.findAvailableGifts("MONTHLY", vipLevel);
    }

    /**
     * 检查礼包是否可领取
     */
    public boolean canClaimGift(User user, VipGift gift) {
        if (!"ACTIVE".equals(gift.getStatus())) {
            return false;
        }
        
        if (user.getVipLevel() < gift.getVipLevelRequired()) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        if ("DAILY".equals(gift.getClaimType())) {
            // 每日礼包：检查 24 小时内是否领取过
            LocalDateTime startTime = now.minusHours(gift.getClaimIntervalHours());
            List<VipGiftRecord> records = vipGiftRecordRepository.findRecentClaims(user, gift.getId(), startTime);
            return records.isEmpty();
        } else if ("MONTHLY".equals(gift.getClaimType())) {
            // 每月礼包：检查 30 天内是否领取过
            LocalDateTime startTime = now.minusHours(gift.getClaimIntervalHours());
            List<VipGiftRecord> records = vipGiftRecordRepository.findRecentClaims(user, gift.getId(), startTime);
            return records.isEmpty();
        } else if ("ONCE".equals(gift.getClaimType())) {
            // 一次性礼包：检查是否领取过
            long count = vipGiftRecordRepository.countByUserAndGift(user, gift.getId());
            return count == 0;
        }
        
        return false;
    }

    /**
     * 领取礼包
     */
    @Transactional
    public Map<String, Object> claimGift(User user, Long giftId) {
        VipGift gift = vipGiftRepository.findById(giftId)
            .orElseThrow(() -> new RuntimeException("礼包不存在"));
        
        if (!canClaimGift(user, gift)) {
            throw new RuntimeException("礼包不可领取");
        }
        
        // 创建领取记录
        VipGiftRecord record = new VipGiftRecord();
        record.setUser(user);
        record.setGift(gift);
        record.setRewardType("CREDIT");
        record.setRewardValue(String.valueOf(gift.getCreditReward()));
        
        vipGiftRecordRepository.save(record);
        
        // 发放奖励
        Map<String, Object> result = new HashMap<>();
        result.put("record", record);
        
        if (gift.getCreditReward() > 0) {
            creditService.addCredit(user.getId(), gift.getCreditReward(), 
                "礼包奖励：" + gift.getName());
            result.put("creditReward", gift.getCreditReward());
        }
        
        // 发放优惠券
        if (gift.getCouponIds() != null && !gift.getCouponIds().isEmpty()) {
            // couponIds 是逗号分隔的字符串，需要解析
            String[] couponIdArray = gift.getCouponIds().split(",");
            List<Long> issuedCouponIds = new ArrayList<>();
            for (String couponIdStr : couponIdArray) {
                try {
                    Long couponId = Long.parseLong(couponIdStr.trim());
                    com.market.entity.Coupon coupon = couponRepository.findById(couponId).orElse(null);
                    if (coupon != null) {
                        com.market.entity.UserCoupon userCoupon = new com.market.entity.UserCoupon(user, coupon);
                        userCoupon.setStatus("UNUSED");
                        userCouponRepository.save(userCoupon);
                        issuedCouponIds.add(couponId);
                    }
                } catch (NumberFormatException e) {
                    // 跳过无效的优惠券ID
                }
            }
            result.put("issuedCouponIds", issuedCouponIds);
        }
        
        return result;
    }

    /**
     * 获取用户的礼包领取记录
     */
    public List<VipGiftRecord> getGiftRecords(User user, String type) {
        if (type != null && !type.isEmpty()) {
            return vipGiftRecordRepository.findByUserAndGiftType(user, type);
        }
        return vipGiftRecordRepository.findByUser(user);
    }

    /**
     * 检查礼包领取状态
     */
    public Map<String, Object> getGiftClaimStatus(User user, VipGift gift) {
        Map<String, Object> status = new HashMap<>();
        status.put("canClaim", canClaimGift(user, gift));
        
        if ("DAILY".equals(gift.getClaimType())) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = now.minusHours(gift.getClaimIntervalHours());
            List<VipGiftRecord> records = vipGiftRecordRepository.findRecentClaims(user, gift.getId(), startTime);
            
            if (!records.isEmpty()) {
                LocalDateTime lastClaimed = records.get(0).getClaimedAt();
                LocalDateTime nextAvailable = lastClaimed.plusHours(gift.getClaimIntervalHours());
                status.put("lastClaimed", lastClaimed);
                status.put("nextAvailable", nextAvailable);
                status.put("remainingSeconds", 
                    java.time.Duration.between(now, nextAvailable).getSeconds());
            }
        } else if ("MONTHLY".equals(gift.getClaimType())) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startTime = now.minusHours(gift.getClaimIntervalHours());
            List<VipGiftRecord> records = vipGiftRecordRepository.findRecentClaims(user, gift.getId(), startTime);
            
            if (!records.isEmpty()) {
                LocalDateTime lastClaimed = records.get(0).getClaimedAt();
                LocalDateTime nextAvailable = lastClaimed.plusHours(gift.getClaimIntervalHours());
                status.put("lastClaimed", lastClaimed);
                status.put("nextAvailable", nextAvailable);
                status.put("remainingDays", 
                    java.time.Duration.between(now, nextAvailable).toDays());
            }
        } else if ("ONCE".equals(gift.getClaimType())) {
            Optional<VipGiftRecord> lastRecord = vipGiftRecordRepository.findByUserAndGiftIdOrderByClaimedAtDesc(user, gift.getId());
            lastRecord.ifPresent(record -> {
                status.put("lastClaimed", record.getClaimedAt());
                status.put("claimed", true);
            });
        }
        
        return status;
    }

    // ==================== VIP 充值 ====================

    /**
     * 创建充值订单
     */
    @Transactional
    public VipRechargeOrder createRechargeOrder(User user, BigDecimal amount) {
        VipRechargeOrder order = new VipRechargeOrder();
        order.setUser(user);
        order.setOrderNo(generateOrderNo());
        order.setAmount(amount);
        // 充值比例：1 元 = 10 成长值
        order.setGrowthValue(amount.intValue());  // 1元=1成长值
        order.setStatus("PENDING");
        
        return vipRechargeOrderRepository.save(order);
    }

    /**
     * 获取充值订单
     */
    public VipRechargeOrder getRechargeOrder(String orderNo) {
        return vipRechargeOrderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
    }

    /**
     * 支付充值订单
     */
    @Transactional
    public VipRechargeOrder payRechargeOrder(String orderNo, String paymentMethod) {
        VipRechargeOrder order = vipRechargeOrderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus("PAID");
        order.setPaymentMethod(paymentMethod);
        order.setPaidAt(LocalDateTime.now());
        
        // 增加成长值
        User user = order.getUser();
        user.setGrowthValue(user.getGrowthValue() + order.getGrowthValue());
        userRepository.save(user);
        
        // 检查充值后是否升级
        VipLevel newLevel = vipLevelRepository.findHighestLevelByGrowthValue(user.getGrowthValue())
            .orElse(null);
        if (newLevel != null && newLevel.getLevel() > user.getVipLevel()) {
            int oldLevel = user.getVipLevel();
            user.setVipLevel(newLevel.getLevel());
            userRepository.save(user);
            
            String[] levelNames = {"普通会员", "白银会员", "黄金会员", "铂金会员", "钻石会员", "至尊会员"};
            String title = "恭喜升至 " + levelNames[newLevel.getLevel()];
            String msg = "充值成功！成长值 +" + order.getGrowthValue() + "，会员等级从 " 
                + levelNames[oldLevel] + " 升级为 " + levelNames[newLevel.getLevel()] + "！";
            try {
                notificationService.sendNotification(user.getId(), title, msg, "SYSTEM", "URGENT",
                    "VIP_RECHARGE_UPGRADE", order.getId(), "/user?tab=vip");
            } catch (Exception e) {}
        }

        return vipRechargeOrderRepository.save(order);
    }

    /**
     * 获取用户充值记录
     */
    public List<VipRechargeOrder> getRechargeHistory(User user) {
        return vipRechargeOrderRepository.findByUser(user);
    }

    /**
     * 获取用户充值统计
     */
    public Map<String, Object> getRechargeStats(User user) {
        Map<String, Object> stats = new HashMap<>();
        
        BigDecimal totalAmount = vipRechargeOrderRepository.getTotalRechargeAmount(user);
        Integer totalGrowth = vipRechargeOrderRepository.getTotalGrowthValue(user);
        
        stats.put("totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO);
        stats.put("totalGrowth", totalGrowth != null ? totalGrowth : 0);
        
        return stats;
    }

    // ==================== 工具方法 ====================

    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        int random = new Random().nextInt(9000) + 1000;
        return "VIP" + timestamp + random;
    }
}

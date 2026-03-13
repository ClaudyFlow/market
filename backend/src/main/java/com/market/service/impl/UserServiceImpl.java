package com.market.service.impl;

import com.market.entity.*;
import com.market.mapper.UserMapper;
import com.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 每日签到基础积分
    private static final int DAILY_CHECKIN_POINTS = 10;

    // VIP 等级配置
    private static final String[] VIP_LEVEL_NAMES = {
        "普通会员", "白银会员", "黄金会员", "铂金会员", "钻石会员", "至尊会员"
    };

    // VIP 权益配置
    private static final Map<Integer, List<String>> VIP_BENEFITS = new HashMap<>();
    static {
        VIP_BENEFITS.put(0, Arrays.asList("基础购物"));
        VIP_BENEFITS.put(1, Arrays.asList("基础购物", "生日礼包"));
        VIP_BENEFITS.put(2, Arrays.asList("基础购物", "生日礼包", "运费券"));
        VIP_BENEFITS.put(3, Arrays.asList("基础购物", "生日礼包", "运费券", "专属客服"));
        VIP_BENEFITS.put(4, Arrays.asList("基础购物", "生日礼包", "运费券", "专属客服", "折扣优惠"));
        VIP_BENEFITS.put(5, Arrays.asList("基础购物", "生日礼包", "运费券", "专属客服", "折扣优惠", "优先发货"));
    }

    @Override
    public VipInfo getVipInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        VipInfo vipInfo = new VipInfo();
        vipInfo.setLevel(user.getVipLevel());
        vipInfo.setLevelName(VIP_LEVEL_NAMES[user.getVipLevel()]);
        vipInfo.setExpireTime(user.getVipExpireTime());
        vipInfo.setBenefits(VIP_BENEFITS.getOrDefault(user.getVipLevel(), VIP_BENEFITS.get(0)));
        vipInfo.setGrowthValue(user.getGrowthValue());
        vipInfo.setNextLevelGrowth(calculateNextLevelGrowth(user.getVipLevel()));

        return vipInfo;
    }

    @Override
    public UserPointsInfo getUserPointsInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        UserPointsInfo pointsInfo = new UserPointsInfo();
        pointsInfo.setPoints(user.getPoints());
        pointsInfo.setTotalPoints(user.getTotalPoints());
        pointsInfo.setConsumedPoints(user.getConsumedPoints());
        
        // 检查今日是否已签到
        boolean hasCheckedIn = hasCheckedInToday(user.getLastCheckInTime());
        pointsInfo.setHasCheckedIn(hasCheckedIn);
        pointsInfo.setConsecutiveDays(user.getConsecutiveCheckinDays());
        pointsInfo.setLastCheckInTime(user.getLastCheckInTime());

        return pointsInfo;
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @Transactional
    public CheckInResult checkIn(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            CheckInResult result = new CheckInResult();
            result.setSuccess(false);
            result.setMessage("用户不存在");
            return result;
        }

        // 检查今日是否已签到
        if (hasCheckedInToday(user.getLastCheckInTime())) {
            CheckInResult result = new CheckInResult();
            result.setSuccess(false);
            result.setHasCheckedIn(true);
            result.setMessage("今日已签到");
            return result;
        }

        // 计算连续签到天数
        int consecutiveDays = calculateConsecutiveDays(user.getLastCheckInTime(), user.getConsecutiveCheckinDays());
        
        // 计算奖励积分（连续签到有额外奖励）
        int bonusPoints = calculateBonusPoints(consecutiveDays);
        int totalPoints = DAILY_CHECKIN_POINTS + bonusPoints;

        // 更新用户积分
        user.setPoints(user.getPoints() + totalPoints);
        user.setTotalPoints(user.getTotalPoints() + totalPoints);
        user.setConsecutiveCheckinDays(consecutiveDays);
        user.setLastCheckInTime(new Date());

        // 更新成长值
        user.setGrowthValue(user.getGrowthValue() + 10);
        
        // 检查是否需要升级 VIP
        updateVipLevel(user);

        userMapper.updateById(user);

        CheckInResult result = new CheckInResult();
        result.setSuccess(true);
        result.setPoints(totalPoints);
        result.setHasCheckedIn(true);
        result.setConsecutiveDays(consecutiveDays);
        result.setMessage(String.format("签到成功！获得 %d 积分，连续签到 %d 天", totalPoints, consecutiveDays));

        return result;
    }

    @Override
    @Transactional
    public boolean consumePoints(Long userId, Integer amount) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getPoints() < amount) {
            return false;
        }

        user.setPoints(user.getPoints() - amount);
        user.setConsumedPoints(user.getConsumedPoints() + amount);
        userMapper.updateById(user);
        return true;
    }

    @Override
    @Transactional
    public boolean addPoints(Long userId, Integer amount) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }

        user.setPoints(user.getPoints() + amount);
        user.setTotalPoints(user.getTotalPoints() + amount);
        userMapper.updateById(user);
        return true;
    }

    /**
     * 检查今日是否已签到
     */
    private boolean hasCheckedInToday(Date lastCheckInTime) {
        if (lastCheckInTime == null) {
            return false;
        }

        Calendar today = Calendar.getInstance();
        Calendar lastCheckIn = Calendar.getInstance();
        lastCheckIn.setTime(lastCheckInTime);

        return today.get(Calendar.YEAR) == lastCheckIn.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == lastCheckIn.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 计算连续签到天数
     */
    private int calculateConsecutiveDays(Date lastCheckInTime, int currentDays) {
        if (lastCheckInTime == null) {
            return 1;
        }

        Calendar today = Calendar.getInstance();
        Calendar lastCheckIn = Calendar.getInstance();
        lastCheckIn.setTime(lastCheckInTime);

        long diffDays = (today.getTimeInMillis() - lastCheckIn.getTimeInMillis()) / (1000 * 60 * 60 * 24);

        if (diffDays == 0) {
            // 今天已签到
            return currentDays;
        } else if (diffDays == 1) {
            // 昨天签到，连续
            return currentDays + 1;
        } else {
            // 中断，重新计算
            return 1;
        }
    }

    /**
     * 计算连续签到奖励积分
     */
    private int calculateBonusPoints(int consecutiveDays) {
        if (consecutiveDays >= 30) {
            return 20; // 连续 30 天奖励 20 积分
        } else if (consecutiveDays >= 14) {
            return 10; // 连续 14 天奖励 10 积分
        } else if (consecutiveDays >= 7) {
            return 5;  // 连续 7 天奖励 5 积分
        }
        return 0;
    }

    /**
     * 计算下一等级所需成长值
     */
    private int calculateNextLevelGrowth(int currentLevel) {
        if (currentLevel >= 5) {
            return 0; // 最高等级
        }
        return (currentLevel + 1) * 1000;
    }

    /**
     * 更新 VIP 等级
     */
    private void updateVipLevel(User user) {
        int growthValue = user.getGrowthValue();
        int newLevel = 0;

        if (growthValue >= 5000) {
            newLevel = 5;
        } else if (growthValue >= 4000) {
            newLevel = 4;
        } else if (growthValue >= 3000) {
            newLevel = 3;
        } else if (growthValue >= 2000) {
            newLevel = 2;
        } else if (growthValue >= 1000) {
            newLevel = 1;
        }

        if (newLevel > user.getVipLevel()) {
            user.setVipLevel(newLevel);
            // 设置 VIP 过期时间为一年后
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            user.setVipExpireTime(calendar.getTime());
        }
    }
}

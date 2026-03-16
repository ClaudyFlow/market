package com.market.service;

import com.market.entity.CreditHistory;
import com.market.entity.User;
import com.market.repository.CreditHistoryRepository;
import com.market.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PointsService {

    private static final Logger log = LoggerFactory.getLogger(PointsService.class);

    private final UserRepository userRepository;
    private final CreditHistoryRepository pointsHistoryRepository;

    public PointsService(UserRepository userRepository, CreditHistoryRepository pointsHistoryRepository) {
        this.userRepository = userRepository;
        this.pointsHistoryRepository = pointsHistoryRepository;
    }

    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param points 增加的积分数量
     * @param reason 积分变化原因
     * @return 是否成功
     */
    @Transactional
    public boolean addPoints(Long userId, Integer points, String reason) {
        return addPoints(userId, points, reason, null);
    }

    /**
     * 增加用户积分（带关联订单）
     * @param userId 用户ID
     * @param points 增加的积分数量
     * @param reason 积分变化原因
     * @param relatedOrderId 关联订单ID
     * @return 是否成功
     */
    @Transactional
    public boolean addPoints(Long userId, Integer points, String reason, String relatedOrderId) {
        if (points <= 0) {
            log.warn("增加积分数量必须大于0: {}", points);
            return false;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("用户不存在: {}", userId);
            return false;
        }

        User user = userOpt.get();
        Integer oldPoints = user.getPoints();
        Integer oldTotalPoints = user.getTotalPoints();

        // 更新用户积分
        user.setPoints(oldPoints + points);
        user.setTotalPoints(oldTotalPoints + points);
        userRepository.save(user);

        // 记录积分历史
        CreditHistory history = new CreditHistory(userId, points, user.getPoints(), reason);
        history.setRelatedOrderId(relatedOrderId);
        pointsHistoryRepository.save(history);

        log.info("用户 {} 积分增加 {}，原因: {}，当前积分: {}", userId, points, reason, user.getPoints());
        return true;
    }

    /**
     * 扣除用户积分
     * @param userId 用户ID
     * @param points 扣除的积分数量
     * @param reason 积分变化原因
     * @return 是否成功
     */
    @Transactional
    public boolean deductPoints(Long userId, Integer points, String reason) {
        return deductPoints(userId, points, reason, null);
    }

    /**
     * 扣除用户积分（带关联订单）
     * @param userId 用户ID
     * @param points 扣除的积分数量
     * @param reason 积分变化原因
     * @param relatedOrderId 关联订单ID
     * @return 是否成功
     */
    @Transactional
    public boolean deductPoints(Long userId, Integer points, String reason, String relatedOrderId) {
        if (points <= 0) {
            log.warn("扣除积分数量必须大于0: {}", points);
            return false;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("用户不存在: {}", userId);
            return false;
        }

        User user = userOpt.get();
        if (user.getPoints() < points) {
            log.warn("用户 {} 积分不足，当前积分: {}，需要扣除: {}", userId, user.getPoints(), points);
            return false;
        }

        Integer oldPoints = user.getPoints();

        // 更新用户积分
        user.setPoints(oldPoints - points);
        userRepository.save(user);

        // 记录积分历史（负数表示扣除）
        CreditHistory history = new CreditHistory(userId, -points, user.getPoints(), reason);
        history.setRelatedOrderId(relatedOrderId);
        pointsHistoryRepository.save(history);

        log.info("用户 {} 积分扣除 {}，原因: {}，当前积分: {}", userId, points, reason, user.getPoints());
        return true;
    }

    /**
     * 获取用户当前积分
     * @param userId 用户ID
     * @return 当前积分，用户不存在返回null
     */
    public Integer getUserPoints(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(User::getPoints).orElse(null);
    }

    /**
     * 获取用户积分历史记录
     * @param userId 用户ID
     * @return 积分历史记录列表，按时间倒序
     */
    public List<CreditHistory> getUserPointsHistory(Long userId) {
        return pointsHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 获取用户积分统计信息
     * @param userId 用户ID
     * @return 积分统计信息数组：[当前积分, 累计积分, 获得总量, 兑换总量]
     */
    public Integer[] getUserPointsStats(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        Integer totalEarned = pointsHistoryRepository.getTotalPointsEarned(userId);
        Integer totalRedeemed = pointsHistoryRepository.getTotalPointsRedeemed(userId);

        return new Integer[]{
            user.getPoints(),      // 当前积分
            user.getTotalPoints(), // 累计积分
            totalEarned,           // 获得总量
            totalRedeemed          // 兑换总量
        };
    }
}
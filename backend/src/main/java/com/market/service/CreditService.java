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
public class CreditService {

    private static final Logger log = LoggerFactory.getLogger(CreditService.class);

    private final UserRepository userRepository;
    private final CreditHistoryRepository creditHistoryRepository;

    public CreditService(UserRepository userRepository, CreditHistoryRepository creditHistoryRepository) {
        this.userRepository = userRepository;
        this.creditHistoryRepository = creditHistoryRepository;
    }

    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param credit 增加的积分数量
     * @param reason 积分变化原因
     * @return 是否成功
     */
    @Transactional
    public boolean addCredit(Long userId, Integer credit, String reason) {
        return addCredit(userId, credit, reason, null);
    }

    /**
     * 增加用户积分（带关联订单）
     * @param userId 用户ID
     * @param credit 增加的积分数量
     * @param reason 积分变化原因
     * @param relatedOrderId 关联订单ID
     * @return 是否成功
     */
    @Transactional
    public boolean addCredit(Long userId, Integer credit, String reason, String relatedOrderId) {
        if (credit <= 0) {
            log.warn("增加积分数量必须大于0: {}", credit);
            return false;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("用户不存在: {}", userId);
            return false;
        }

        User user = userOpt.get();
        Integer oldCredit = user.getCredit();
        Integer oldTotalCredit = user.getTotalCredit();

        // 更新用户积分
        user.setCredit(oldCredit + credit);
        user.setTotalCredit(oldTotalCredit + credit);
        userRepository.save(user);

        // 记录积分历史
        CreditHistory history = new CreditHistory(userId, credit, user.getCredit(), reason);
        history.setRelatedOrderId(relatedOrderId);
        creditHistoryRepository.save(history);

        log.info("用户 {} 积分增加 {}，原因: {}，当前积分: {}", userId, credit, reason, user.getCredit());
        return true;
    }

    /**
     * 扣除用户积分
     * @param userId 用户ID
     * @param credit 扣除的积分数量
     * @param reason 积分变化原因
     * @return 是否成功
     */
    @Transactional
    public boolean deductCredit(Long userId, Integer credit, String reason) {
        return deductCredit(userId, credit, reason, null);
    }

    /**
     * 扣除用户积分（带关联订单）
     * @param userId 用户ID
     * @param credit 扣除的积分数量
     * @param reason 积分变化原因
     * @param relatedOrderId 关联订单ID
     * @return 是否成功
     */
    @Transactional
    public boolean deductCredit(Long userId, Integer credit, String reason, String relatedOrderId) {
        if (credit <= 0) {
            log.warn("扣除积分数量必须大于0: {}", credit);
            return false;
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.warn("用户不存在: {}", userId);
            return false;
        }

        User user = userOpt.get();
        if (user.getCredit() < credit) {
            log.warn("用户 {} 积分不足，当前积分: {}，需要扣除: {}", userId, user.getCredit(), credit);
            return false;
        }

        Integer oldCredit = user.getCredit();

        // 更新用户积分
        user.setCredit(oldCredit - credit);
        userRepository.save(user);

        // 记录积分历史（负数表示扣除）
        CreditHistory history = new CreditHistory(userId, -credit, user.getCredit(), reason);
        history.setRelatedOrderId(relatedOrderId);
        creditHistoryRepository.save(history);

        log.info("用户 {} 积分扣除 {}，原因: {}，当前积分: {}", userId, credit, reason, user.getCredit());
        return true;
    }

    /**
     * 获取用户当前积分
     * @param userId 用户ID
     * @return 当前积分，用户不存在返回null
     */
    public Integer getCredit(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(User::getCredit).orElse(null);
    }

    /**
     * 获取用户积分历史记录
     * @param userId 用户ID
     * @return 积分历史记录列表，按时间倒序
     */
    public List<CreditHistory> getCreditHistory(Long userId) {
        return creditHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 获取用户积分统计信息
     * @param userId 用户ID
     * @return 积分统计信息数组：[当前积分, 累计积分, 获得总量, 兑换总量]
     */
    public Integer[] getCreditStats(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        Integer totalEarned = creditHistoryRepository.getTotalCreditEarned(userId);
        Integer totalRedeemed = creditHistoryRepository.getTotalCreditRedeemed(userId);

        return new Integer[]{
            user.getCredit(),      // 当前积分
            user.getTotalCredit(), // 累计积分
            totalEarned,           // 获得总量
            totalRedeemed          // 兑换总量
        };
    }
}
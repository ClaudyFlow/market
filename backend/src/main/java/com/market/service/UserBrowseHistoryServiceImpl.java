package com.market.service;

import com.market.dto.UserBrowseHistoryResponse;
import com.market.entity.UserBrowseHistory;
import com.market.repository.UserBrowseHistoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户浏览历史服务实现类
 *
 * @author Market Team
 * @since 1.0.0
 */
@Service
public class UserBrowseHistoryServiceImpl implements UserBrowseHistoryService {

    @Autowired
    private UserBrowseHistoryRepository userBrowseHistoryRepository;

    @Override
    @Transactional
    public void addBrowseHistory(Long userId, Long productId, String productName,
                                  String productImage, java.math.BigDecimal productPrice,
                                  Long shopId, String shopName) {
        // 检查是否已存在，存在则更新浏览时间
        if (userBrowseHistoryRepository.existsByUserIdAndProductId(userId, productId)) {
            userBrowseHistoryRepository.findByUserIdAndProductId(userId, productId)
                    .ifPresent(history -> {
                        history.setBrowseTime(LocalDateTime.now());
                        history.setProductName(productName);
                        history.setProductImage(productImage);
                        history.setProductPrice(productPrice);
                        history.setShopName(shopName);
                        userBrowseHistoryRepository.save(history);
                    });
        } else {
            UserBrowseHistory history = new UserBrowseHistory();
            history.setUserId(userId);
            history.setProductId(productId);
            history.setProductName(productName);
            history.setProductImage(productImage);
            history.setProductPrice(productPrice);
            history.setShopId(shopId);
            history.setShopName(shopName);
            userBrowseHistoryRepository.save(history);
        }
    }

    @Override
    public Page<UserBrowseHistoryResponse> getBrowseHistory(Long userId, Pageable pageable) {
        return userBrowseHistoryRepository.findByUserIdOrderByBrowseTimeDesc(userId, pageable)
                .map(this::convertToResponse);
    }

    @Override
    public List<UserBrowseHistoryResponse> getBrowseHistoryList(Long userId) {
        return userBrowseHistoryRepository.findByUserIdOrderByBrowseTimeDesc(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserBrowseHistoryResponse> getRecentHistory(Long userId, int limit) {
        return userBrowseHistoryRepository.findRecentHistory(userId, limit)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteHistory(Long userId, Long productId) {
        userBrowseHistoryRepository.deleteByUserIdAndProductId(userId, productId);
        return true;
    }

    @Override
    @Transactional
    public boolean clearHistory(Long userId) {
        userBrowseHistoryRepository.deleteByUserId(userId);
        return true;
    }

    @Override
    public long getHistoryCount(Long userId) {
        return userBrowseHistoryRepository.countByUserId(userId);
    }

    @Override
    public Page<UserBrowseHistoryResponse> searchHistory(Long userId, String keyword, Pageable pageable) {
        return userBrowseHistoryRepository.searchByUserId(userId, keyword, pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional
    public int cleanOldHistory(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return userBrowseHistoryRepository.deleteOldHistory(cutoffDate);
    }

    /**
     * 将 UserBrowseHistory 转换为 UserBrowseHistoryResponse
     */
    private UserBrowseHistoryResponse convertToResponse(UserBrowseHistory history) {
        UserBrowseHistoryResponse response = new UserBrowseHistoryResponse();
        response.setId(history.getId());
        response.setUserId(history.getUserId());
        response.setProductId(history.getProductId());
        response.setProductName(history.getProductName());
        response.setProductImage(history.getProductImage());
        response.setProductPrice(history.getProductPrice());
        response.setShopId(history.getShopId());
        response.setShopName(history.getShopName());
        response.setBrowseTime(history.getBrowseTime());
        return response;
    }
}

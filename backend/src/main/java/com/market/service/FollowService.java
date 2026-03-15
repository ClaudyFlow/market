package com.market.service;

import com.market.entity.Follow;
import com.market.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 关注服务类
 */
@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    /**
     * 添加关注
     */
    public Follow addFavorite(Long userId, Long shopId, String shopName, String shopAvatar) {
        // 检查是否已关注
        if (followRepository.existsByUserIdAndShopId(userId, shopId)) {
            throw new RuntimeException("该店铺已在关注列表中");
        }

        Follow follow = new Follow(userId, shopId, shopName);
        follow.setShopAvatar(shopAvatar);
        return followRepository.save(follow);
    }

    /**
     * 取消关注
     */
    public void removeFavorite(Long userId, Long shopId) {
        followRepository.deleteByUserIdAndShopId(userId, shopId);
    }

    /**
     * 获取用户的关注列表
     */
    public List<Follow> getFavorites(Long userId) {
        return followRepository.findByUserId(userId);
    }

    /**
     * 检查是否已关注
     */
    public boolean isFavorite(Long userId, Long shopId) {
        return followRepository.existsByUserIdAndShopId(userId, shopId);
    }

    /**
     * 获取关注数量
     */
    public int getFavoriteCount(Long userId) {
        return followRepository.countByUserId(userId);
    }

    /**
     * 切换关注状态（关注/取消关注）
     */
    public boolean toggleFavorite(Long userId, Long shopId, String shopName, String shopAvatar) {
        if (followRepository.existsByUserIdAndShopId(userId, shopId)) {
            removeFavorite(userId, shopId);
            return false;
        } else {
            addFavorite(userId, shopId, shopName, shopAvatar);
            return true;
        }
    }
}

package com.market.service;

import com.market.entity.Favorite;
import com.market.entity.Product;
import com.market.repository.FavoriteRepository;
import com.market.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏服务类
 */
@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * 添加收藏
     */
    public Favorite addFavorite(Long userId, Long productId) {
        // 检查是否已收藏
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("该商品已在收藏夹中");
        }
        
        // 检查商品是否存在
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("商品不存在");
        }
        
        Favorite favorite = new Favorite(userId, productId);
        return favoriteRepository.save(favorite);
    }

    /**
     * 取消收藏
     */
    public void removeFavorite(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }

    /**
     * 获取用户的收藏列表
     */
    public List<Favorite> getFavorites(Long userId) {
        return favoriteRepository.findByUserIdWithProduct(userId);
    }

    /**
     * 检查是否已收藏
     */
    public boolean isFavorite(Long userId, Long productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    /**
     * 获取收藏数量
     */
    public int getFavoriteCount(Long userId) {
        return favoriteRepository.countByUserId(userId);
    }

    /**
     * 切换收藏状态（收藏/取消收藏）
     */
    public boolean toggleFavorite(Long userId, Long productId) {
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            removeFavorite(userId, productId);
            return false;
        } else {
            addFavorite(userId, productId);
            return true;
        }
    }
}

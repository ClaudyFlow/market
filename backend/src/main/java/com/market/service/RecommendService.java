package com.market.service;

import com.market.entity.Product;
import com.market.entity.UserBrowseHistory;
import com.market.repository.OrderRepositoryCustom;
import com.market.repository.ProductRepository;
import com.market.repository.UserBrowseHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务类（简化版推荐算法）
 */
@Service
public class RecommendService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserBrowseHistoryRepository browseHistoryRepository;

    @Autowired
    private OrderRepositoryCustom orderRepository;

    /**
     * 获取推荐商品列表（基于浏览历史和购买历史）
     */
    public List<Product> getRecommendProducts(Long userId, int limit) {
        if (userId == null) {
            // 未登录用户，返回热门商品
            return getHotProducts(limit);
        }

        // 1. 获取用户浏览过的商品
        List<UserBrowseHistory> histories = browseHistoryRepository.findByUserIdOrderByBrowseTimeDesc(userId);
        List<Long> browsedProductIds = histories.stream()
                .map(h -> h.getProductId())
                .limit(20)
                .collect(Collectors.toList());

        // 2. 获取用户购买过的商品
        List<Long> purchasedProductIds = orderRepository.findPurchasedProductIds(userId);

        // 3. 获取用户喜欢的商家（购买过的商家）
        List<Long> favoriteMerchantIds = orderRepository.findFavoriteMerchantIds(userId);

        // 4. 获取浏览商品的分类
        List<String> favoriteCategories = new ArrayList<>();
        if (!browsedProductIds.isEmpty()) {
            List<Product> browsedProducts = productRepository.findAllById(browsedProductIds);
            favoriteCategories = browsedProducts.stream()
                    .map(Product::getCategory)
                    .distinct()
                    .collect(Collectors.toList());
        }

        // 5. 推荐策略：
        // - 优先推荐同分类商品（排除已浏览和已购买）
        // - 其次推荐同商家商品
        // - 最后补充热门商品
        
        Set<Long> excludeIds = new HashSet<>();
        excludeIds.addAll(browsedProductIds);
        excludeIds.addAll(purchasedProductIds);

        List<Product> recommended = new ArrayList<>();

        // 推荐同分类商品
        if (!favoriteCategories.isEmpty()) {
            for (String category : favoriteCategories) {
                List<Product> categoryProducts = productRepository.findByCategory(category);
                for (Product p : categoryProducts) {
                    if (!excludeIds.contains(p.getId()) && recommended.size() < limit) {
                        recommended.add(p);
                        excludeIds.add(p.getId());
                    }
                }
            }
        }

        // 推荐同商家商品
        if (!favoriteMerchantIds.isEmpty()) {
            for (Long merchantId : favoriteMerchantIds) {
                List<Product> merchantProducts = productRepository.findByMerchantId(merchantId);
                for (Product p : merchantProducts) {
                    if (!excludeIds.contains(p.getId()) && recommended.size() < limit) {
                        recommended.add(p);
                        excludeIds.add(p.getId());
                    }
                }
            }
        }

        // 补充热门商品
        if (recommended.size() < limit) {
            List<Product> hotProducts = getHotProducts(limit - recommended.size());
            for (Product p : hotProducts) {
                if (!excludeIds.contains(p.getId())) {
                    recommended.add(p);
                    excludeIds.add(p.getId());
                }
            }
        }

        return recommended.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 获取热门商品（按销量排序）
     */
    public List<Product> getHotProducts(int limit) {
        return productRepository.findTop10ByStatusOrderBySalesDesc(1)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取看了又看（基于浏览历史）
     */
    public List<Product> getViewedAlsoViewed(Long userId, Long productId, int limit) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return getHotProducts(limit);
        }

        // 推荐同分类商品
        List<Product> sameCategory = productRepository.findByCategory(product.getCategory());
        return sameCategory.stream()
                .filter(p -> !p.getId().equals(productId))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取买了又买（基于购买历史）
     */
    public List<Product> getBoughtAlsoBought(Long userId, Long productId, int limit) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return getHotProducts(limit);
        }

        // 推荐同类目商品
        List<Product> sameCategory = productRepository.findByCategory(product.getCategory());
        return sameCategory.stream()
                .filter(p -> !p.getId().equals(productId))
                .filter(p -> p.getSales() > 0) // 有销量的商品
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取店铺推荐商品
     */
    public List<Product> getShopRecommend(Long merchantId, int limit) {
        List<Product> merchantProducts = productRepository.findByMerchantId(merchantId);
        return merchantProducts.stream()
                .sorted(Comparator.comparing(Product::getSales).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}

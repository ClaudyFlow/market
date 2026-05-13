package com.market.service;

import com.market.annotation.AuditLog;
import com.market.annotation.Cacheable;
import com.market.annotation.DistributedLock;
import com.market.entity.Shop;
import com.market.entity.User;
import com.market.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 店铺服务类
 */
@Service
@Transactional
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    /**
     * 获取店铺列表
     */
    @Cacheable(key = "'shop_list_' + #pageable.pageNumber + '_' + #pageable.pageSize", 
               cacheName = "shops", expire = 300)
    @AuditLog(module = "店铺管理", action = "查询店铺列表")
    public Page<Shop> getShops(Pageable pageable) {
        return shopRepository.findByStatus("active", pageable);
    }

    /**
     * 获取店铺详情
     */
    @Cacheable(key = "'shop_detail_' + #id", cacheName = "shops", expire = 600)
    @AuditLog(module = "店铺管理", action = "查询店铺详情")
    public Shop getShopDetail(Long id) {
        return shopRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("店铺不存在"));
    }

    /**
     * 根据店主获取店铺
     */
    @Cacheable(key = "'shop_owner_' + #ownerId", cacheName = "shops", expire = 600)
    public Optional<Shop> getShopByOwnerId(Long ownerId) {
        return shopRepository.findByOwnerId(ownerId);
    }

    /**
     * 创建店铺
     */
    @DistributedLock(key = "'create_shop_' + #owner.id", waitTime = 5000)
    @AuditLog(module = "店铺管理", action = "创建店铺", recordParams = true)
    public Shop createShop(User owner, Shop shop) {
        // 检查是否已有店铺
        Optional<Shop> existingShop = shopRepository.findByOwnerId(owner.getId());
        if (existingShop.isPresent()) {
            throw new RuntimeException("该用户已拥有店铺");
        }

        shop.setOwner(owner);
        shop.setRating(BigDecimal.ZERO);
        shop.setFollowers(0);
        shop.setProductCount(0);
        shop.setPositiveRate(BigDecimal.ZERO);
        shop.setOpenYears(0);
        shop.setStatus("active");
        shop.setCertified(false);

        return shopRepository.save(shop);
    }

    /**
     * 更新店铺信息
     */
    @DistributedLock(key = "'update_shop_' + #id", waitTime = 5000)
    @AuditLog(module = "店铺管理", action = "更新店铺", recordParams = true)
    public Shop updateShop(Long id, User owner, Shop shop) {
        Shop existingShop = getShopDetail(id);
        
        // 检查权限
        if (!existingShop.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("无权修改该店铺");
        }

        if (shop.getName() != null) existingShop.setName(shop.getName());
        if (shop.getLogo() != null) existingShop.setLogo(shop.getLogo());
        if (shop.getBanner() != null) existingShop.setBanner(shop.getBanner());
        if (shop.getDescription() != null) existingShop.setDescription(shop.getDescription());
        if (shop.getSlogan() != null) existingShop.setSlogan(shop.getSlogan());
        if (shop.getAnnouncement() != null) existingShop.setAnnouncement(shop.getAnnouncement());
        if (shop.getTags() != null) existingShop.setTags(shop.getTags());
        if (shop.getLocation() != null) existingShop.setLocation(shop.getLocation());

        existingShop.setUpdatedAt(LocalDateTime.now());
        return shopRepository.save(existingShop);
    }

    /**
     * 更新店铺公告
     */
    @AuditLog(module = "店铺管理", action = "更新店铺公告")
    public Shop updateAnnouncement(Long id, String announcement) {
        Shop shop = getShopDetail(id);
        shop.setAnnouncement(announcement);
        shop.setUpdatedAt(LocalDateTime.now());
        return shopRepository.save(shop);
    }

    /**
     * 删除店铺
     */
    @DistributedLock(key = "'delete_shop_' + #id", waitTime = 5000)
    @AuditLog(module = "店铺管理", action = "删除店铺")
    public void deleteShop(Long id, User owner) {
        Shop shop = getShopDetail(id);
        if (!shop.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("无权删除该店铺");
        }
        shopRepository.delete(shop);
    }

    /**
     * 搜索店铺
     */
    @Cacheable(key = "'shop_search_' + #keyword + '_' + #pageable.pageNumber", 
               cacheName = "shops", expire = 300)
    public Page<Shop> searchShops(String keyword, Pageable pageable) {
        return shopRepository.searchShops(keyword, pageable);
    }

    /**
     * 获取认证店铺
     */
    @Cacheable(key = "'shop_certified'", cacheName = "shops", expire = 600)
    public List<Shop> getCertifiedShops() {
        return shopRepository.findByCertifiedAndStatus(true, "active");
    }

    /**
     * 获取高评分店铺
     */
    @Cacheable(key = "'shop_high_rating_' + #minRating", cacheName = "shops", expire = 300)
    public Page<Shop> getHighRatingShops(Double minRating, Pageable pageable) {
        return shopRepository.findByRatingGreaterThanEqual(minRating, pageable);
    }

    /**
     * 关注店铺
     */
    @DistributedLock(key = "'follow_shop_' + #id + '_' + #userId", waitTime = 3000)
    @AuditLog(module = "店铺管理", action = "关注店铺")
    public Shop followShop(Long id, Long userId) {
        Shop shop = getShopDetail(id);
        shop.setFollowers(shop.getFollowers() + 1);
        return shopRepository.save(shop);
    }

    /**
     * 取消关注店铺
     */
    @DistributedLock(key = "'unfollow_shop_' + #id + '_' + #userId", waitTime = 3000)
    @AuditLog(module = "店铺管理", action = "取消关注店铺")
    public Shop unfollowShop(Long id, Long userId) {
        Shop shop = getShopDetail(id);
        shop.setFollowers(Math.max(0, shop.getFollowers() - 1));
        return shopRepository.save(shop);
    }

    /**
     * 更新店铺评分
     */
    @AuditLog(module = "店铺管理", action = "更新店铺评分")
    public Shop updateRating(Long id, Double rating) {
        Shop shop = getShopDetail(id);
        // 简单平均（实际应该更复杂）
        BigDecimal newRating = shop.getRating()
            .multiply(BigDecimal.valueOf(shop.getFollowers()))
            .add(BigDecimal.valueOf(rating))
            .divide(BigDecimal.valueOf(shop.getFollowers() + 1), 2, java.math.RoundingMode.HALF_UP);
        shop.setRating(newRating);
        return shopRepository.save(shop);
    }

    /**
     * 更新服务评分
     */
    public Shop updateServiceScores(Long id, Double descriptionScore, Double serviceScore, Double logisticsScore) {
        Shop shop = getShopDetail(id);
        shop.setDescriptionScore(BigDecimal.valueOf(descriptionScore));
        shop.setServiceScore(BigDecimal.valueOf(serviceScore));
        shop.setLogisticsScore(BigDecimal.valueOf(logisticsScore));
        return shopRepository.save(shop);
    }

    /**
     * 认证商家
     */
    @DistributedLock(key = "'certify_shop_' + #id", waitTime = 5000)
    @AuditLog(module = "店铺管理", action = "认证商家")
    public Shop certifyShop(Long id, String businessLicense) {
        Shop shop = getShopDetail(id);
        shop.setCertified(true);
        shop.setBusinessLicense(businessLicense);
        return shopRepository.save(shop);
    }

    /**
     * 关闭店铺
     */
    @DistributedLock(key = "'close_shop_' + #id", waitTime = 5000)
    @AuditLog(module = "店铺管理", action = "关闭店铺")
    public Shop closeShop(Long id, String reason) {
        Shop shop = getShopDetail(id);
        shop.setStatus("closed");
        return shopRepository.save(shop);
    }

    /**
     * 获取店铺统计
     */
    @Cacheable(key = "'shop_stats_' + #id", cacheName = "shops", expire = 300)
    public Map<String, Object> getShopStats(Long id) {
        Shop shop = getShopDetail(id);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", shop.getProductCount());
        stats.put("followers", shop.getFollowers());
        stats.put("rating", shop.getRating());
        stats.put("positiveRate", shop.getPositiveRate());
        stats.put("openYears", shop.getOpenYears());
        return stats;
    }

    /**
     * 检查店铺是否属于用户
     */
    public boolean isOwner(Shop shop, User user) {
        return shop.getOwner() != null && shop.getOwner().getId().equals(user.getId());
    }
}

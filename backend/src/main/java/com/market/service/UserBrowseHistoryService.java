package com.market.service;

import com.market.dto.UserBrowseHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 用户浏览历史服务接口
 *
 * @author Market Team
 * @since 1.0.0
 */
public interface UserBrowseHistoryService {

    /**
     * 添加浏览历史
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @param productName 商品名称
     * @param productImage 商品图片
     * @param productPrice 商品价格
     * @param shopId 店铺 ID
     * @param shopName 店铺名称
     */
    void addBrowseHistory(Long userId, Long productId, String productName,
                          String productImage, java.math.BigDecimal productPrice,
                          Long shopId, String shopName);

    /**
     * 获取浏览历史（分页）
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 浏览历史分页
     */
    Page<UserBrowseHistoryResponse> getBrowseHistory(Long userId, Pageable pageable);

    /**
     * 获取浏览历史列表
     *
     * @param userId 用户 ID
     * @return 浏览历史列表
     */
    List<UserBrowseHistoryResponse> getBrowseHistoryList(Long userId);

    /**
     * 获取最近浏览记录
     *
     * @param userId 用户 ID
     * @param limit 数量限制
     * @return 浏览历史列表
     */
    List<UserBrowseHistoryResponse> getRecentHistory(Long userId, int limit);

    /**
     * 删除浏览历史
     *
     * @param userId 用户 ID
     * @param productId 商品 ID
     * @return 是否成功
     */
    boolean deleteHistory(Long userId, Long productId);

    /**
     * 清空浏览历史
     *
     * @param userId 用户 ID
     * @return 是否成功
     */
    boolean clearHistory(Long userId);

    /**
     * 获取浏览历史数量
     *
     * @param userId 用户 ID
     * @return 浏览历史数量
     */
    long getHistoryCount(Long userId);

    /**
     * 搜索浏览历史
     *
     * @param userId 用户 ID
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 浏览历史分页
     */
    Page<UserBrowseHistoryResponse> searchHistory(Long userId, String keyword, Pageable pageable);

    /**
     * 清理旧浏览历史
     *
     * @param days 保留天数
     * @return 删除数量
     */
    int cleanOldHistory(int days);
}

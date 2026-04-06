package com.market.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户浏览历史响应 DTO
 * 用于返回用户浏览过的商品详细信息
 *
 * @author market-team
 * @since 1.0
 */
@Data
public class UserBrowseHistoryResponse {
    /** 浏览记录ID */
    private Long id;
    /** 用户ID */
    private Long userId;
    /** 商品ID */
    private Long productId;
    /** 商品名称 */
    private String productName;
    /** 商品图片URL */
    private String productImage;
    /** 商品价格 */
    private java.math.BigDecimal productPrice;
    /** 店铺ID */
    private Long shopId;
    /** 店铺名称 */
    private String shopName;
    /** 浏览时间 */
    private LocalDateTime browseTime;
}

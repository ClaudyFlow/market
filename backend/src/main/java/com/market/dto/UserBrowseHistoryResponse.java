package com.market.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户浏览历史响应 DTO
 */
@Data
public class UserBrowseHistoryResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private String productImage;
    private LocalDateTime browseTime;
}

package com.market.service;

import com.market.entity.ProductImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 商品图片服务接口
 */
public interface ProductImageService {

    /**
     * 保存图片（Base64）
     * @param productId 商品 ID
     * @param base64Data Base64 图片数据
     * @param isCompressed 是否已压缩
     * @return 保存后的图片实体
     */
    ProductImage saveImage(Long productId, String base64Data, Boolean isCompressed);

    /**
     * 保存多张图片
     * @param productId 商品 ID
     * @param base64List Base64 图片列表
     * @return 保存后的图片列表
     */
    List<ProductImage> saveImages(Long productId, List<String> base64List);

    /**
     * 根据商品 ID 获取所有图片
     */
    List<ProductImage> getImagesByProductId(Long productId);

    /**
     * 获取商品主图
     */
    ProductImage getMainImage(Long productId);

    /**
     * 删除图片
     */
    void deleteImage(Long imageId);

    /**
     * 删除商品的所有图片
     */
    void deleteByProductId(Long productId);

    /**
     * 生成缩略图（前端已压缩，后端只生成更小的缩略图）
     */
    String generateThumbnail(String base64Data, int size);
}

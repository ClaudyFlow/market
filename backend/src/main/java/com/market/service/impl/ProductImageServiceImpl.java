package com.market.service.impl;

import com.market.entity.ProductImage;
import com.market.repository.ProductImageRepository;
import com.market.service.ProductImageService;
import com.market.util.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 商品图片服务实现
 */
@Service
public class ProductImageServiceImpl implements ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    private static final Pattern BASE64_PATTERN = Pattern.compile("^data:image/(\\w+);base64,(.+)$");

    @Override
    @Transactional
    public ProductImage saveImage(Long productId, String base64Data, Boolean isCompressed) {
        // 验证 Base64 格式
        if (!Base64Util.isValidImageBase64(base64Data)) {
            throw new IllegalArgumentException("无效的 Base64 图片格式");
        }

        // 解析 Base64 信息
        ImageInfo info = parseBase64Info(base64Data);

        // 创建实体
        ProductImage image = new ProductImage();
        image.setProductId(productId);
        image.setImageData(base64Data);
        image.setFileSize((int) Base64Util.getSizeInBytes(base64Data));
        image.setFormat(info.format);
        image.setIsCompressed(isCompressed != null && isCompressed);

        // 生成缩略图（200x200）
        String thumbnail = generateThumbnail(base64Data, 200);
        image.setThumbnailData(thumbnail);

        return productImageRepository.save(image);
    }

    @Override
    @Transactional
    public List<ProductImage> saveImages(Long productId, List<String> base64List) {
        List<ProductImage> images = new ArrayList<>();
        for (String base64 : base64List) {
            try {
                ProductImage image = saveImage(productId, base64, true);
                images.add(image);
            } catch (Exception e) {
                // 记录错误，继续处理其他图片
                System.err.println("保存图片失败：" + e.getMessage());
            }
        }
        return images;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImage> getImagesByProductId(Long productId) {
        return productImageRepository.findByProductIdOrderByCreatedAtAsc(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductImage getMainImage(Long productId) {
        return productImageRepository.findFirstByProductIdOrderByCreatedAtAsc(productId);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        productImageRepository.deleteById(imageId);
    }

    @Override
    @Transactional
    public void deleteByProductId(Long productId) {
        productImageRepository.deleteByProductId(productId);
    }

    @Override
    public String generateThumbnail(String base64Data, int size) {
        // 注意：实际缩略图生成应该在前端完成
        // 后端这里只返回原图，或者可以做简单的缩放
        // 由于 Java 处理图片需要额外依赖（如 Thumbnailator），这里暂时返回原图
        
        // TODO: 如果需要在后端生成缩略图，可以添加 Thumbnailator 依赖
        // 但目前推荐在前端用 Canvas 生成，性能更好
        
        return base64Data; // 暂时返回原图
    }

    /**
     * 解析 Base64 图片信息
     */
    private ImageInfo parseBase64Info(String base64Data) {
        ImageInfo info = new ImageInfo();
        
        Matcher matcher = BASE64_PATTERN.matcher(base64Data);
        if (matcher.matches()) {
            info.format = matcher.group(1);
        } else {
            info.format = "jpeg";
        }

        // 获取图片尺寸（需要解析 Base64）
        try {
            String pureBase64 = Base64Util.removePrefix(base64Data);
            byte[] imageBytes = Base64.getDecoder().decode(pureBase64);
            
            // 简单判断图片格式
            if (imageBytes.length > 10) {
                // PNG 文件头
                if (imageBytes[0] == (byte) 0x89 && imageBytes[1] == (byte) 0x50) {
                    info.format = "png";
                }
                // JPEG 文件头
                else if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8) {
                    info.format = "jpeg";
                }
                // WEBP 文件头
                else if (imageBytes[8] == (byte) 0x57 && imageBytes[9] == (byte) 0x45) {
                    info.format = "webp";
                }
            }
        } catch (Exception e) {
            // 解析失败，使用默认值
        }

        return info;
    }

    /**
     * 图片信息内部类
     */
    private static class ImageInfo {
        String format = "jpeg";
        int width = 0;
        int height = 0;
    }
}

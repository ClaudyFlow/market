package com.market.controller;

import com.market.annotation.RateLimiter;
import com.market.common.Result;
import com.market.entity.ProductImage;
import com.market.service.ProductImageService;
import com.market.util.ImageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件上传控制器
 * 只负责存储 Base64 数据，不做任何压缩处理
 */
@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @Autowired
    private ProductImageService productImageService;

    /**
     * 接收并存储 Base64 图片
     * 前端已经压缩好，后端只负责存储
     */
    @PostMapping("/image")
    @RateLimiter(maxRequests = 10, timeout = 60, message = "上传过于频繁，请稍后再试")
    public Result<Map<String, Object>> uploadImage(@RequestBody Map<String, Object> body) {
        try {
            String base64Data = (String) body.get("base64");
            Boolean compressed = (Boolean) body.get("compressed");
            Long productId = body.get("productId") != null ? 
                Long.parseLong(body.get("productId").toString()) : null;
            
            // 安全验证
            ImageValidator.ValidationResult validation = ImageValidator.validate(
                base64Data, 
                5.0, // 最大 5MB
                new String[]{"png", "jpg", "jpeg", "webp"}
            );
            
            if (!validation.isValid()) {
                return Result.error(validation.getMessage());
            }
            
            // 保存到数据库（如果有 productId）
            ProductImage savedImage = null;
            if (productId != null) {
                savedImage = productImageService.saveImage(productId, base64Data, compressed);
            }
            
            // 生成文件信息
            Map<String, Object> result = new HashMap<>();
            result.put("url", base64Data);
            result.put("filename", generateFilename() + ".jpg");
            result.put("compressed", compressed != null && compressed);
            result.put("storedAt", System.currentTimeMillis());
            
            if (savedImage != null) {
                result.put("id", savedImage.getId());
                result.put("thumbnail", savedImage.getThumbnailData());
            }
            
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量接收 Base64 图片
     */
    @PostMapping("/images/batch")
    public Result<Map<String, Object>> uploadImages(
        @RequestBody Map<String, Object> body,
        @RequestParam(value = "productId", required = false) Long productId
    ) {
        try {
            @SuppressWarnings("unchecked")
            List<String> images = (List<String>) body.get("images");
            
            if (images == null || images.isEmpty()) {
                return Result.error("图片数据不能为空");
            }
            
            // 保存到数据库
            List<ProductImage> savedImages = null;
            if (productId != null) {
                List<String> validImages = images.stream()
                    .filter(img -> img != null && img.startsWith("data:image"))
                    .collect(Collectors.toList());
                savedImages = productImageService.saveImages(productId, validImages);
            }
            
            // 返回结果
            Map<String, Object> result = new HashMap<>();
            if (savedImages != null) {
                result.put("images", savedImages.stream().map(img -> {
                    Map<String, Object> imgData = new HashMap<>();
                    imgData.put("id", img.getId());
                    imgData.put("url", img.getImageData());
                    imgData.put("thumbnail", img.getThumbnailData());
                    return imgData;
                }).collect(Collectors.toList()));
            }
            result.put("total", savedImages != null ? savedImages.size() : images.size());
            
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取商品图片列表
     */
    @GetMapping("/product/{productId}/images")
    public Result<List<Map<String, Object>>> getProductImages(@PathVariable Long productId) {
        try {
            List<ProductImage> images = productImageService.getImagesByProductId(productId);
            
            List<Map<String, Object>> result = images.stream().map(img -> {
                Map<String, Object> imgData = new HashMap<>();
                imgData.put("id", img.getId());
                imgData.put("url", img.getImageData());
                imgData.put("thumbnail", img.getThumbnailData());
                imgData.put("width", img.getWidth());
                imgData.put("height", img.getHeight());
                imgData.put("format", img.getFormat());
                imgData.put("fileSize", img.getFileSize());
                return imgData;
            }).collect(Collectors.toList());
            
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 删除图片（从数据库）
     */
    @DeleteMapping("/image/{imageId}")
    public Result<Void> deleteImage(@PathVariable Long imageId) {
        try {
            productImageService.deleteImage(imageId);
            return Result.success(null);
            
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 生成唯一文件名
     */
    private String generateFilename() {
        return System.currentTimeMillis() + "_" + 
               UUID.randomUUID().toString().substring(0, 8);
    }
}

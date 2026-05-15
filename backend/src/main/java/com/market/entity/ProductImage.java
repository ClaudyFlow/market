package com.market.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品图片实体类（Base64存储）
 * 对应数据库表：product_image
 *
 * @author market-team
 * @since 1.0
 */
@Data
@Entity
@Table(name = "product_image", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class ProductImage {

    /**
     * 图片记录唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的商品 ID
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * Base64 图片数据（原图）
     */
    @Lob
    @Column(name = "image_data", columnDefinition = "TEXT", nullable = false)
    private String imageData;

    /**
     * Base64 缩略图数据
     */
    @Lob
    @Column(name = "thumbnail_data", columnDefinition = "TEXT")
    private String thumbnailData;

    /**
     * 文件大小（字节）
     */
    @Column(name = "file_size")
    private Integer fileSize;

    /**
     * 图片宽度
     */
    @Column(name = "width")
    private Integer width;

    /**
     * 图片高度
     */
    @Column(name = "height")
    private Integer height;

    /**
     * 图片格式 (jpeg, png, webp)
     */
    @Column(name = "format", length = 10)
    private String format;

    /**
     * 是否已压缩
     */
    @Column(name = "is_compressed")
    private Boolean isCompressed = false;

    /**
     * 压缩率 (0-100)
     */
    @Column(name = "compression_rate")
    private Integer compressionRate;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Lombok @Data 会自动生成 getter/setter，但为了确保编译通过，手动添加
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getImageData() { return imageData; }
    public void setImageData(String imageData) { this.imageData = imageData; }
    
    public String getThumbnailData() { return thumbnailData; }
    public void setThumbnailData(String thumbnailData) { this.thumbnailData = thumbnailData; }
    
    public Integer getFileSize() { return fileSize; }
    public void setFileSize(Integer fileSize) { this.fileSize = fileSize; }
    
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public Boolean getIsCompressed() { return isCompressed; }
    public void setIsCompressed(Boolean isCompressed) { this.isCompressed = isCompressed; }
    
    public Integer getCompressionRate() { return compressionRate; }
    public void setCompressionRate(Integer compressionRate) { this.compressionRate = compressionRate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

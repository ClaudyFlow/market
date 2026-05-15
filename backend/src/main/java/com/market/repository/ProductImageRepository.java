package com.market.repository;

import com.market.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品图片数据访问层
 * 对应实体：ProductImage
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    /**
     * 根据商品 ID 查询所有图片
     */
    List<ProductImage> findByProductIdOrderByCreatedAtAsc(Long productId);

    /**
     * 根据商品 ID 查询第一张图片（主图）
     */
    ProductImage findFirstByProductIdOrderByCreatedAtAsc(Long productId);

    /**
     * 删除商品的所有图片
     */
    @Modifying
    @Query("DELETE FROM ProductImage pi WHERE pi.productId = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    /**
     * 统计商品的图片数量
     */
    long countByProductId(Long productId);

    /**
     * 查询已压缩的图片
     */
    List<ProductImage> findByProductIdAndIsCompressedTrue(Long productId);
}

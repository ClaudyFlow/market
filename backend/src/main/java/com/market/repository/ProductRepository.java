package com.market.repository;

import com.market.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品数据访问接口
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 按分类获取商品
     */
    List<Product> findByCategory(String category);

    /**
     * 获取可售商品
     */
    List<Product> findByAvailableTrue();

    /**
     * 按名称搜索商品
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * 按分类获取可售商品
     */
    List<Product> findByCategoryAndAvailableTrue(String category);

    /**
     * 搜索商品
     */
    @Query("SELECT p FROM Product p WHERE p.status = 1 AND " +
           "(p.name LIKE %:keyword% OR p.description LIKE %:keyword% OR p.category LIKE %:keyword%) " +
           "ORDER BY p.createdAt DESC")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据商品名称模糊查询
     */
    List<Product> findByNameContaining(String keyword);

    /**
     * 按商家 ID 获取商品
     */
    Page<Product> findByMerchantId(Long merchantId, Pageable pageable);

    /**
     * 按分类和状态获取商品
     */
    Page<Product> findByCategoryAndStatus(String category, Integer status, Pageable pageable);

    /**
     * 统计商品数量
     */
    long countByStatus(Integer status);

    /**
     * 统计可售商品数量
     */
    long countByAvailableTrue();

    /**
     * 按状态获取商品
     */
    Page<Product> findByStatus(Integer status, Pageable pageable);
}

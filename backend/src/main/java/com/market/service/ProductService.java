package com.market.service;

import com.market.entity.Product;
import com.market.entity.User;
import com.market.repository.ProductRepository;
import com.market.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品服务类
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取商品列表
     */
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findByStatus(1, pageable);
    }

    /**
     * 按分类获取商品
     */
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryAndStatus(category, 1, pageable);
    }

    /**
     * 获取商品详情
     */
    public Product getProductDetail(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
    }

    /**
     * 获取分类列表（从数据库读取）
     */
    public List<String> getCategories() {
        return categoryService.getAllActiveCategories().stream()
            .map(c -> c.getName())
            .toList();
    }

    /**
     * 搜索商品
     */
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable);
    }

    /**
     * 根据 ID 列表获取商品
     */
    public List<Product> getProductsByIds(List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    /**
     * 商家获取商品列表
     */
    public Page<Product> getMerchantProducts(User merchant, String status, Integer auditStatus, Pageable pageable) {
        return productRepository.findByMerchantId(merchant.getId(), pageable);
    }

    /**
     * 获取商品（商家）
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
    }

    /**
     * 创建商品
     */
    @Transactional
    public Product createProduct(User merchant, Product product) {
        product.setMerchant(merchant);
        product.setStatus(1);
        product.setAuditStatus(0);
        return productRepository.save(product);
    }

    /**
     * 更新商品
     */
    @Transactional
    public Product updateProduct(Long id, User merchant, Product product) {
        Product existingProduct = getProductById(id);
        if (!existingProduct.getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("无权修改该商品");
        }

        if (product.getName() != null) existingProduct.setName(product.getName());
        if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());
        if (product.getPrice() != null) existingProduct.setPrice(product.getPrice());
        if (product.getStock() != null) existingProduct.setStock(product.getStock());
        if (product.getCategory() != null) existingProduct.setCategory(product.getCategory());
        if (product.getImageUrl() != null) existingProduct.setImageUrl(product.getImageUrl());

        return productRepository.save(existingProduct);
    }

    /**
     * 更新商品
     */
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProductById(id);
        if (product.getName() != null) existingProduct.setName(product.getName());
        if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());
        if (product.getPrice() != null) existingProduct.setPrice(product.getPrice());
        if (product.getStock() != null) existingProduct.setStock(product.getStock());
        if (product.getCategory() != null) existingProduct.setCategory(product.getCategory());
        if (product.getImageUrl() != null) existingProduct.setImageUrl(product.getImageUrl());
        return productRepository.save(existingProduct);
    }

    /**
     * 删除商品
     */
    @Transactional
    public void deleteProduct(Long id, User merchant) {
        Product product = getProductById(id);
        if (!product.getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("无权删除该商品");
        }
        productRepository.delete(product);
    }

    /**
     * 管理员删除商品
     */
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * 审核商品
     */
    @Transactional
    public Product auditProduct(Long id, Boolean approved, String rejectReason) {
        Product product = getProductById(id);
        if (approved) {
            product.setStatus(1);
            product.setAuditStatus(1);
        } else {
            product.setStatus(0);
            product.setAuditStatus(2);
            product.setRejectReason(rejectReason);
        }
        return productRepository.save(product);
    }

    /**
     * 下架商品
     */
    @Transactional
    public Product takeProductOffline(Long id, String reason) {
        Product product = getProductById(id);
        product.setStatus(0);
        return productRepository.save(product);
    }

    /**
     * 获取待审核商品
     */
    public Page<Product> getAuditProducts(Integer status, Long merchantId, Pageable pageable) {
        return productRepository.findByStatus(status, pageable);
    }

    /**
     * 获取审核统计
     */
    public Map<String, Object> getAuditStats() {
        return new HashMap<>();
    }

    /**
     * 检查商品是否属于商家
     */
    public boolean isMerchantProduct(Product product, User merchant) {
        return product.getMerchant() != null && product.getMerchant().getId().equals(merchant.getId());
    }
    
    /**
     * 获取用户今日分享次数
     */
    public int getTodayShareCount(Long userId) {
        return 0; // 默认无限制，实际可查 ProductShareLog 表
    }
    
    /**
     * 记录分享行为
     */
    @Transactional
    public void recordShare(Long userId, Long productId) {
        // 实际可记录到 ProductShareLog 表用于统计
    }

}

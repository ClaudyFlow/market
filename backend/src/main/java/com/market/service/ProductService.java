package com.market.service;

import com.market.entity.Product;
import com.market.entity.User;
import com.market.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品服务类
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findByAvailableTrue();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndAvailableTrue(category);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());
        product.setImageUrl(productDetails.getImageUrl());
        product.setAvailable(productDetails.getAvailable());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public boolean reduceStock(Long productId, Integer quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            if (product.getStock() >= quantity) {
                product.setStock(product.getStock() - quantity);
                productRepository.save(product);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取商家的商品列表
     */
    public Page<Product> getMerchantProducts(User merchant, String keyword, Integer status, Pageable pageable) {
        List<Product> allProducts = productRepository.findAll().stream()
            .filter(p -> merchant.equals(p.getMerchant()))
            .filter(p -> keyword == null || keyword.isEmpty() || p.getName().toLowerCase().contains(keyword.toLowerCase()))
            .filter(p -> status == null || p.getStatus().equals(status))
            .sorted(Comparator.comparing(Product::getCreatedAt).reversed())
            .collect(Collectors.toList());

        int start = (int) pageable.getOffset() * pageable.getPageSize();
        int end = Math.min(start + pageable.getPageSize(), allProducts.size());
        List<Product> pageContent = allProducts.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allProducts.size());
    }

    /**
     * 检查商品是否属于该商户
     */
    public boolean isMerchantProduct(Product product, User merchant) {
        return product.getMerchant() != null && product.getMerchant().getId().equals(merchant.getId());
    }

    /**
     * 商家创建商品
     */
    @Transactional
    public Product createProduct(User merchant, Product product) {
        product.setMerchant(merchant);
        product.setStatus(1); // 默认上架
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    /**
     * 商家更新商品
     */
    @Transactional
    public Product updateProduct(Long id, User merchant, Product productDetails) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!isMerchantProduct(product, merchant)) {
            throw new RuntimeException("无权操作该商品");
        }

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setOriginalPrice(productDetails.getOriginalPrice());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());
        product.setImageUrl(productDetails.getImageUrl());
        product.setStatus(productDetails.getStatus());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    /**
     * 商家删除商品
     */
    @Transactional
    public void deleteProduct(Long id, User merchant) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!isMerchantProduct(product, merchant)) {
            throw new RuntimeException("无权操作该商品");
        }

        productRepository.delete(product);
    }

    /**
     * 获取待审核商品列表
     */
    public Page<Product> getAuditProducts(String keyword, Long merchantId, Pageable pageable) {
        List<Product> allProducts = productRepository.findAll().stream()
            .filter(p -> p.getAuditStatus() == null || p.getAuditStatus() == 0)
            .filter(p -> keyword == null || keyword.isEmpty() || p.getName().toLowerCase().contains(keyword.toLowerCase()))
            .filter(p -> merchantId == null || (p.getMerchant() != null && p.getMerchant().getId().equals(merchantId)))
            .sorted(Comparator.comparing(Product::getCreatedAt).reversed())
            .collect(Collectors.toList());

        int start = (int) pageable.getOffset() * pageable.getPageSize();
        int end = Math.min(start + pageable.getPageSize(), allProducts.size());
        List<Product> pageContent = allProducts.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allProducts.size());
    }

    /**
     * 审核商品
     */
    @Transactional
    public Product auditProduct(Long id, Boolean approved, String rejectReason) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        product.setAuditStatus(approved ? 1 : 2);
        product.setRejectReason(rejectReason);
        product.setUpdatedAt(LocalDateTime.now());

        if (approved && product.getStatus() == null) {
            product.setStatus(1); // 审核通过，自动上架
        }

        return productRepository.save(product);
    }

    /**
     * 获取商品审核统计
     */
    public Map<String, Object> getAuditStats() {
        List<Product> allProducts = productRepository.findAll();
        long pendingCount = allProducts.stream()
            .filter(p -> p.getAuditStatus() == null || p.getAuditStatus() == 0)
            .count();
        long approvedCount = allProducts.stream()
            .filter(p -> p.getAuditStatus() != null && p.getAuditStatus() == 1)
            .count();
        long rejectedCount = allProducts.stream()
            .filter(p -> p.getAuditStatus() != null && p.getAuditStatus() == 2)
            .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("pending", pendingCount);
        stats.put("approved", approvedCount);
        stats.put("rejected", rejectedCount);
        stats.put("total", (long) allProducts.size());

        return stats;
    }

    /**
     * 强制下架商品
     */
    @Transactional
    public Product takeProductOffline(Long id, String reason) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        product.setStatus(0); // 下架
        product.setRejectReason(reason);
        product.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(product);
    }

    public void seedSampleData() {
        if (productRepository.count() == 0) {
            String[][] product = {
                {"无线蓝牙耳机", "高品质音质，长续航", "299", "100", "电子产品"},
                {"纯棉 T 恤", "舒适透气，多色可选", "99", "200", "服装"},
                {"智能手表", "多功能运动监测", "599", "50", "电子产品"},
                {"休闲运动鞋", "轻便舒适，适合日常穿着", "199", "150", "服装"},
                {"不锈钢保温杯", "24 小时保温保冷", "79", "300", "生活用品"},
                {"机械键盘", "Cherry 轴体，RGB 背光", "399", "80", "电子产品"},
                {"双肩背包", "大容量，防水面料", "159", "120", "箱包"},
                {"LED 台灯", "护眼调光，USB 充电", "89", "200", "生活用品"},
                {"牛仔裤", "经典款式，修身版型", "199", "180", "服装"},
                {"无线鼠标", "静音设计，长续航", "129", "250", "电子产品"}
            };

            for (String[] p : product) {
                Product productItem = new Product();
                productItem.setName(p[0]);
                productItem.setDescription(p[1]);
                productItem.setPrice(new BigDecimal(p[2]));
                productItem.setStock(Integer.parseInt(p[3]));
                productItem.setCategory(p[4]);
                productItem.setImageUrl("/images/product.jpg");
                productItem.setAvailable(true);
                productRepository.save(productItem);
            }
        }
    }
}

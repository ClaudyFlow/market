package com.market.service;

import com.market.entity.Product;
import com.market.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
    
    public void seedSampleData() {
        if (productRepository.count() == 0) {
            String[][] products = {
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
            
            for (String[] p : products) {
                Product product = new Product();
                product.setName(p[0]);
                product.setDescription(p[1]);
                product.setPrice(new BigDecimal(p[2]));
                product.setStock(Integer.parseInt(p[3]));
                product.setCategory(p[4]);
                product.setImageUrl("/images/product.jpg");
                product.setAvailable(true);
                productRepository.save(product);
            }
        }
    }
}

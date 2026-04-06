package com.market.service;

import com.market.entity.CartItem;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.repository.CartItemRepository;
import com.market.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 购物车服务类
 */
@Service
public class CartService {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }
    
    @Transactional
    public CartItem addToCart(User user, Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        
        List<CartItem> existingItems = cartItemRepository.findByUser(user);
        for (CartItem item : existingItems) {
            if (item.getProduct().getId().equals(productId)) {
                int newQuantity = item.getQuantity() + quantity;
                if (product.getStock() < newQuantity) {
                    throw new RuntimeException("库存不足");
                }
                item.setQuantity(newQuantity);
                return cartItemRepository.save(item);
            }
        }
        
        CartItem newItem = new CartItem(user, product, quantity);
        return cartItemRepository.save(newItem);
    }
    
    @Transactional
    public CartItem updateQuantity(User user, Long itemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(itemId)
            .filter(i -> i.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
        
        Product product = item.getProduct();
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }
        
        if (quantity <= 0) {
            cartItemRepository.delete(item);
            return null;
        }
        
        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }
    
    @Transactional
    public void removeFromCart(User user, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
            .filter(i -> i.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
        cartItemRepository.delete(item);
    }
    
    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }
    
    public BigDecimal getCartTotal(User user) {
        List<CartItem> item = getCartItems(user);
        return item.stream()
            .map(CartItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getCartItemCount(User user) {
        return getCartItems(user).size();
    }

    /**
     * 选中/取消选中购物车商品
     */
    @Transactional
    public CartItem selectItem(User user, Long itemId, Boolean selected) {
        CartItem item = cartItemRepository.findById(itemId)
            .filter(i -> i.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("购物车商品不存在"));
        
        item.setSelected(selected);
        return cartItemRepository.save(item);
    }

    /**
     * 全选/取消全选
     */
    @Transactional
    public void selectAll(User user, Boolean selected) {
        List<CartItem> items = cartItemRepository.findByUser(user);
        for (CartItem item : items) {
            item.setSelected(selected);
        }
        cartItemRepository.saveAll(items);
    }

    /**
     * 获取选中的商品列表
     */
    public List<CartItem> getSelectedItems(User user) {
        return cartItemRepository.findByUserAndSelectedTrue(user);
    }

    /**
     * 检查购物车商品库存
     * @return 库存不足的商品列表
     */
    public List<CartItem> checkStock(User user) {
        List<CartItem> items = getCartItems(user);
        return items.stream()
            .filter(item -> item.getProduct().getStock() < item.getQuantity())
            .toList();
    }
}

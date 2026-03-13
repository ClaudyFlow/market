package com.market.controller;

import com.market.entity.CartItem;
import com.market.entity.User;
import com.market.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems(@AuthenticationPrincipal User user) {
        List<CartItem> items = cartService.getCartItems(user);
        return ResponseEntity.ok(items);
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @AuthenticationPrincipal User user,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        try {
            CartItem item = cartService.addToCart(user, productId, quantity);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        try {
            CartItem item = cartService.updateQuantity(user, id, quantity);
            if (item == null) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        cartService.removeFromCart(user, id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getCartTotal(@AuthenticationPrincipal User user) {
        List<CartItem> items = cartService.getCartItems(user);
        BigDecimal total = cartService.getCartTotal(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("itemCount", items.size());
        response.put("totalAmount", total);
        
        return ResponseEntity.ok(response);
    }
}

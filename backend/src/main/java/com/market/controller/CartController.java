package com.market.controller;

import com.market.annotation.*;
import com.market.common.Result;
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
 * 提供购物车的增删改查、选中状态管理、库存检查等功能。
 * 权限要求：需要登录（用户端）
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/cart
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 获取购物车
     * API路径：GET /api/cart
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 购物车商品列表
     */
    @GetMapping
    @Cacheable(key = "'cart_' + #user.id", cacheName = "cart", expire = 300)
    @AuditLog(module = "购物车管理", action = "查询购物车")
    public Result<List<CartItem>> getCartItems(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        List<CartItem> items = cartService.getCartItems(user);
        return Result.success(items);
    }

    /**
     * 添加商品到购物车
     * API路径：POST /api/cart/add
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param productId 商品ID
     * @param quantity 添加数量，默认1
     * @return 添加后的购物车商品项
     */
    @PostMapping("/add")
    @Idempotent(key = "'add_cart_' + #user.id + '_' + #productId", expire = 600, message = "正在添加到购物车，请勿重复提交")
    @DistributedLock(key = "'add_cart_' + #user.id + '_' + #productId", waitTime = 3000)
    @AuditLog(module = "购物车管理", action = "添加到购物车", recordParams = true)
    @Retryable(maxAttempts = 3, delay = 500)
    public Result<CartItem> addToCart(
            @AuthenticationPrincipal User user,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            CartItem item = cartService.addToCart(user, productId, quantity);
            return Result.success(item);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 更新购物车商品数量
     * API路径：PUT /api/cart/update/{id}
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param id 购物车商品项ID
     * @param quantity 新的数量
     * @return 更新后的购物车商品项
     */
    @PutMapping("/update/{id}")
    @Idempotent(key = "'update_cart_' + #id", expire = 600)
    @DistributedLock(key = "'update_cart_' + #id", waitTime = 3000)
    @AuditLog(module = "购物车管理", action = "更新购物车数量")
    public Result<CartItem> updateQuantity(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            CartItem item = cartService.updateQuantity(user, id, quantity);
            if (item == null) {
                return Result.success(null);
            }
            return Result.success(item);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 删除购物车商品
     * API路径：DELETE /api/cart/remove/{id}
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param id 购物车商品项ID
     * @return 操作结果
     */
    @DeleteMapping("/remove/{id}")
    @Idempotent(key = "'remove_cart_' + #id", expire = 600)
    @AuditLog(module = "购物车管理", action = "删除购物车商品")
    public Result<Void> removeFromCart(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        cartService.removeFromCart(user, id);
        return Result.success(null);
    }

    /**
     * 清空购物车
     * API路径：DELETE /api/cart/clear
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    @Idempotent(key = "'clear_cart_' + #user.id", expire = 600)
    @DistributedLock(key = "'clear_cart_' + #user.id", waitTime = 3000)
    @AuditLog(module = "购物车管理", action = "清空购物车")
    public Result<Void> clearCart(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }

        cartService.clearCart(user);
        return Result.success(null);
    }

    /**
     * 获取购物车商品总数和总金额
     * API路径：GET /api/cart/total
     * 权限：需要登录（未登录返回0）
     *
     * @param user 当前登录用户
     * @return 商品数量和总金额
     */
    @GetMapping("/total")
    @Cacheable(key = "'cart_total_' + #user.id", cacheName = "cart", expire = 60)
    @AuditLog(module = "购物车管理", action = "查询购物车数量")
    public Result<Map<String, Object>> getCartTotal(@AuthenticationPrincipal User user) {
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("itemCount", 0);
            response.put("totalAmount", 0);
            return Result.success(response);
        }

        List<CartItem> items = cartService.getCartItems(user);
        BigDecimal total = cartService.getCartTotal(user);

        Map<String, Object> response = new HashMap<>();
        response.put("itemCount", items.size());
        response.put("totalAmount", total);

        return Result.success(response);
    }

    /**
     * 选中/取消选中购物车商品
     * API路径：PUT /api/cart/select/{id}
     * 权限：需要登录
     *
     * @param id 购物车商品项ID
     * @param selected 是否选中
     * @param user 当前登录用户
     * @return 更新后的购物车商品项
     */
    @PutMapping("/select/{id}")
    @Idempotent(key = "'select_cart_' + #id + '_' + #selected", expire = 600)
    @AuditLog(module = "购物车管理", action = "选中购物车商品")
    public Result<CartItem> selectItem(
            @PathVariable Long id,
            @RequestParam Boolean selected,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            CartItem item = cartService.selectItem(user, id, selected);
            return Result.success(item);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 全选/取消全选购物车商品
     * API路径：PUT /api/cart/select-all
     * 权限：需要登录
     *
     * @param selected 是否全选
     * @param user 当前登录用户
     * @return 操作结果
     */
    @PutMapping("/select-all")
    @Idempotent(key = "'select_all_cart_' + #user.id + '_' + #selected", expire = 600)
    @AuditLog(module = "购物车管理", action = "全选购物车商品")
    public Result<Void> selectAll(
            @RequestParam Boolean selected,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        cartService.selectAll(user, selected);
        return Result.success(null);
    }

    /**
     * 获取选中商品列表
     * API路径：GET /api/cart/selected
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 选中的购物车商品列表
     */
    @GetMapping("/selected")
    @Cacheable(key = "'cart_selected_' + #user.id", cacheName = "cart", expire = 60)
    @AuditLog(module = "购物车管理", action = "查询选中商品")
    public Result<List<CartItem>> getSelectedItems(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }

        List<CartItem> selectedItems = cartService.getSelectedItems(user);
        return Result.success(selectedItems);
    }

    /**
     * 检查购物车商品库存
     * API路径：GET /api/cart/check-stock
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 库存无效的商品列表和是否全部有效
     */
    @GetMapping("/check-stock")
    @AuditLog(module = "购物车管理", action = "检查购物车库存")
    @RateLimiter(key = "'check_cart_stock_' + #user.id", maxRequests = 30, timeout = 60, timeUnit = java.util.concurrent.TimeUnit.SECONDS)
    public Result<Map<String, Object>> checkStock(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }

        List<CartItem> invalidItems = cartService.checkStock(user);
        Map<String, Object> result = new HashMap<>();
        result.put("invalidItems", invalidItems);
        result.put("allValid", invalidItems.isEmpty());
        return Result.success(result);
    }
}

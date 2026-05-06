package com.market.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 购物车实体类
 * 对应数据库表：cart_item
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "cart_item")
public class CartItem {

    /**
     * 购物车项唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 购物车中的商品
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * 商品数量
     */
    @Column(nullable = false)
    private Integer quantity = 1;

    /**
     * 是否选中结算
     */
    @Column(nullable = false)
    private Boolean selected = true;

    public CartItem() {}

    public CartItem(User user, Product product, Integer quantity) {
        this.user = user;
        this.product = product;
        this.quantity = quantity;
        this.selected = true;
    }
    
    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(new BigDecimal(quantity));
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
     
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Boolean getSelected() { return selected; }
    public void setSelected(Boolean selected) { this.selected = selected; }
}

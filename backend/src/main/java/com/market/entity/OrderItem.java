package com.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 订单项实体类
 * 对应数据库表：order_item
 *
 * @author market-team
 * @since 1.0
 */
@Entity
@Table(name = "order_item")
public class OrderItem {

    /**
     * 订单项唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属订单
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * 商品
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * 购买数量
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * 商品单价
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    public OrderItem() {}
    
    public OrderItem(Product product, Integer quantity, BigDecimal price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
    
    public BigDecimal getSubtotal() {
        return price.multiply(new BigDecimal(quantity));
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}

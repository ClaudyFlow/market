package com.market.repository;

import com.market.entity.CartItem;
import com.market.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 购物车项数据访问层
 * 对应实体：CartItem
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * 根据用户查询购物车项列表
     *
     * @param user 用户对象
     * @return 购物车项列表
     */
    List<CartItem> findByUser(User user);

    /**
     * 删除用户的所有购物车项
     *
     * @param user 用户对象
     */
    void deleteByUser(User user);

    /**
     * 查询用户已选中的购物车项列表
     *
     * @param user 用户对象
     * @return 已选中的购物车项列表
     */
    List<CartItem> findByUserAndSelectedTrue(User user);

    /**
     * 根据用户和选中状态查询购物车项
     *
     * @param user 用户对象
     * @param selected 选中状态
     * @return 购物车项列表
     */
    List<CartItem> findByUserAndSelected(User user, Boolean selected);

    /**
     * 批量更新用户购物车项的选中状态
     *
     * @param user 用户对象
     * @param selected 选中状态
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE CartItem c SET c.selected = :selected WHERE c.user = :user")
    void updateSelectedByUser(@org.springframework.data.repository.query.Param("user") User user, @org.springframework.data.repository.query.Param("selected") Boolean selected);

    /**
     * 更新单个购物车项的选中状态
     *
     * @param id 购物车项ID
     * @param user 用户对象
     * @param selected 选中状态
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE CartItem c SET c.selected = :selected WHERE c.id = :id AND c.user = :user")
    void updateSelectedById(@org.springframework.data.repository.query.Param("id") Long id, @org.springframework.data.repository.query.Param("user") User user, @org.springframework.data.repository.query.Param("selected") Boolean selected);
}

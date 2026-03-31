package com.market.repository;

import com.market.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 * <p>
 * 提供用户实体的数据访问操作，包括基本的 CRUD 操作和自定义查询方法。
 * 继承 JpaRepository 以获得标准的 JPA 数据访问功能。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据邮箱查找用户
     *
     * @param email 用户邮箱
     * @return 包含用户的 Optional 对象，如果未找到则为空
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据用户名查找用户
     *
     * @param name 用户名
     * @return 包含用户的 Optional 对象，如果未找到则为空
     */
    Optional<User> findByName(String name);

    /**
     * 检查邮箱是否存在
     *
     * @param email 要检查的邮箱
     * @return 如果邮箱已存在返回 true，否则返回 false
     */
    boolean existsByEmail(String email);

    /**
     * 检查用户名是否存在
     *
     * @param name 要检查的用户名
     * @return 如果用户名已存在返回 true，否则返回 false
     */
    boolean existsByName(String name);

    /**
     * 根据手机号查找用户
     *
     * @param phone 手机号
     * @return 包含用户的 Optional 对象
     */
    Optional<User> findByPhone(String phone);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 如果手机号已存在返回 true
     */
    boolean existsByPhone(String phone);

    /**
     * 搜索用户（按用户名、邮箱、手机号）
     *
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 用户列表分页
     */
    @Query("SELECT u FROM User u WHERE " +
           "u.name LIKE %:keyword% OR u.email LIKE %:keyword% OR u.phone LIKE %:keyword%")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 获取活跃用户（最近登录）
     *
     * @param sinceTime 起始时间
     * @return 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt > :sinceTime ORDER BY u.lastLoginAt DESC")
    List<User> findActiveUsers(@Param("sinceTime") LocalDateTime sinceTime);

    /**
     * 统计用户总数
     *
     * @return 用户总数
     */
    long count();

    /**
     * 统计活跃用户数（最近登录）
     *
     * @param sinceTime 起始时间
     * @return 活跃用户数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLoginAt > :sinceTime")
    long countActiveUsers(@Param("sinceTime") LocalDateTime sinceTime);

    /**
     * 统计今日新增用户
     *
     * @param todayStart 今日开始时间
     * @return 今日新增用户数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt > :todayStart")
    long countTodayNewUsers(@Param("todayStart") LocalDateTime todayStart);

    /**
     * 获取 VIP 用户列表
     *
     * @param minLevel 最低 VIP 等级
     * @return VIP 用户列表
     */
    @Query("SELECT u FROM User u WHERE u.vipLevel >= :minLevel ORDER BY u.vipLevel DESC")
    List<User> findVipUsers(@Param("minLevel") Integer minLevel);

    /**
     * 获取积分排行用户
     *
     * @param pageable 分页参数
     * @return 积分排行用户列表
     */
    @Query("SELECT u FROM User u ORDER BY u.credit DESC")
    Page<User> findTopUsersByCredit(Pageable pageable);

    /**
     * 根据角色获取用户
     *
     * @param role 角色
     * @return 用户列表
     */
    List<User> findByRole(String role);

    /**
     * 获取商家用户
     *
     * @return 商家用户列表
     */
    @Query("SELECT u FROM User u WHERE u.isMerchant = true")
    List<User> findMerchants();

    /**
     * 检查店铺名称是否存在
     *
     * @param shopName 要检查的店铺名称
     * @return 如果店铺名称已存在返回 true
     */
    boolean existsByShopName(String shopName);
}

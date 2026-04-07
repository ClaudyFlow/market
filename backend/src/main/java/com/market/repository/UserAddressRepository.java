package com.market.repository;

import com.market.entity.UserAddress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户收货地址数据访问层
 * 对应实体：UserAddress
 *
 * @author market-team
 * @since 1.0
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

    /**
     * 获取用户的所有地址
     *
     * @param userId 用户 ID
     * @return 地址列表
     */
    List<UserAddress> findByUserId(Long userId);

    /**
     * 获取用户的所有地址（分页）
     *
     * @param userId 用户 ID
     * @param pageable 分页参数
     * @return 地址列表分页
     */
    Page<UserAddress> findByUserId(Long userId, Pageable pageable);

    /**
     * 获取用户的默认地址
     *
     * @param userId 用户 ID
     * @return 默认地址
     */
    UserAddress findByUserIdAndIsDefaultTrue(Long userId);

    /**
     * 检查用户是否有默认地址
     *
     * @param userId 用户 ID
     * @return 如果存在默认地址返回 true
     */
    boolean existsByUserIdAndIsDefaultTrue(Long userId);

    /**
     * 获取用户的地址数量
     *
     * @param userId 用户 ID
     * @return 地址数量
     */
    long countByUserId(Long userId);

    /**
     * 删除用户的地址
     *
     * @param userId 用户 ID
     */
    void deleteByUserId(Long userId);

    /**
     * 搜索用户地址
     *
     * @param userId 用户 ID
     * @param keyword 搜索关键词（收货人姓名、手机号、地址）
     * @param pageable 分页参数
     * @return 地址列表分页
     */
    @Query("SELECT ua FROM UserAddress ua WHERE ua.userId = :userId " +
           "AND (ua.receiverName LIKE CONCAT('%', :keyword, '%') OR ua.receiverPhone LIKE CONCAT('%', :keyword, '%') " +
           "OR ua.province LIKE CONCAT('%', :keyword, '%') OR ua.city LIKE CONCAT('%', :keyword, '%') " +
           "OR ua.detailAddress LIKE CONCAT('%', :keyword, '%')) " +
           "ORDER BY ua.isDefault DESC, ua.createdAt DESC")
    Page<UserAddress> searchByUserId(@Param("userId") Long userId,
                                      @Param("keyword") String keyword,
                                      Pageable pageable);
}

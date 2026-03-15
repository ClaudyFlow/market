package com.market.repository;

import com.market.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /**
     * 根据用户 ID 和店铺 ID 查找关注
     */
    List<Follow> findByUserIdAndShopId(Long userId, Long shopId);

    /**
     * 根据用户 ID 查找所有关注
     */
    List<Follow> findByUserId(Long userId);

    /**
     * 根据用户 ID 查找所有关注（带用户信息）
     */
    @Query("SELECT f FROM Follow f JOIN FETCH f.user WHERE f.userId = :userId")
    List<Follow> findByUserIdWithUser(Long userId);

    /**
     * 检查用户是否关注了某店铺
     */
    boolean existsByUserIdAndShopId(Long userId, Long shopId);

    /**
     * 删除用户的某个关注
     */
    void deleteByUserIdAndShopId(Long userId, Long shopId);

    /**
     * 统计用户关注数量
     */
    int countByUserId(Long userId);
}

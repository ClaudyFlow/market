package com.market.repository;

import com.market.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户关注关系数据访问接口
 *
 * @author Market Team
 * @since 1.0.0
 */
@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    /**
     * 查找用户是否关注了另一个用户
     *
     * @param followerId 关注者 ID
     * @param followingId 被关注者 ID
     * @return 包含关注关系的 Optional 对象
     */
    Optional<UserFollow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * 查找用户关注的所有用户 ID
     *
     * @param followerId 关注者 ID
     * @return 被关注者 ID 列表
     */
    List<UserFollow> findByFollowerId(Long followerId);

    /**
     * 查找关注某用户的所有用户 ID
     *
     * @param followingId 被关注者 ID
     * @return 关注者 ID 列表
     */
    List<UserFollow> findByFollowingId(Long followingId);

    /**
     * 统计用户关注的人数
     *
     * @param followerId 关注者 ID
     * @return 关注人数
     */
    long countByFollowerId(Long followerId);

    /**
     * 统计用户的粉丝数
     *
     * @param followingId 被关注者 ID
     * @return 粉丝数
     */
    long countByFollowingId(Long followingId);

    /**
     * 检查用户是否关注了另一个用户
     *
     * @param followerId 关注者 ID
     * @param followingId 被关注者 ID
     * @return 如果已关注返回 true，否则返回 false
     */
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    /**
     * 获取用户关注的用户列表（分页查询用）
     *
     * @param followerId 关注者 ID
     * @return 关注关系列表
     */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.followerId = :followerId ORDER BY uf.createdAt DESC")
    List<UserFollow> findFollowingList(@Param("followerId") Long followerId);

    /**
     * 获取用户的粉丝列表（分页查询用）
     *
     * @param followingId 被关注者 ID
     * @return 关注关系列表
     */
    @Query("SELECT uf FROM UserFollow uf WHERE uf.followingId = :followingId ORDER BY uf.createdAt DESC")
    List<UserFollow> findFollowerList(@Param("followingId") Long followingId);
}

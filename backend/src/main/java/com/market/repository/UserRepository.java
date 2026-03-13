package com.market.repository;

import com.market.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 用户数据访问接口
 * <p>
 * 提供用户实体的数据访问操作，包括基本的CRUD操作和自定义查询方法。
 * 继承JpaRepository以获得标准的JPA数据访问功能。
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
     * @return 包含用户的Optional对象，如果未找到则为空
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据用户名查找用户
     *
     * @param name 用户名
     * @return 包含用户的Optional对象，如果未找到则为空
     */
    Optional<User> findByName(String name);

    /**
     * 检查邮箱是否存在
     *
     * @param email 要检查的邮箱
     * @return 如果邮箱已存在返回true，否则返回false
     */
    boolean existsByEmail(String email);

    /**
     * 检查用户名是否存在
     *
     * @param name 要检查的用户名
     * @return 如果用户名已存在返回true，否则返回false
     */
    boolean existsByName(String name);
}
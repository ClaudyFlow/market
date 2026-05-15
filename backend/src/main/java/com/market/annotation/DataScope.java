package com.market.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 *
 * 用于标记需要数据权限控制的方法
 * 自动过滤用户只能访问其权限范围内的数据
 *
 * @example
 * {@code
 * @DataScope(
 *     deptAlias = "d",
 *     userAlias = "u",
 *     scopeType = ScopeType.DEPT
 * )
 * public List<User> getUserList() { ... }
 * }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 部门别名（用于 SQL 拼接）
     */
    String deptAlias() default "";

    /**
     * 用户别名（用于 SQL 拼接）
     */
    String userAlias() default "";

    /**
     * 权限范围类型
     */
    ScopeType scopeType() default ScopeType.CUSTOM;

    /**
     * 权限范围枚举
     */
    enum ScopeType {
        /**
         * 全部数据权限
         */
        ALL,
        /**
         * 本部门数据权限
         */
        DEPT,
        /**
         * 本部门及子部门数据权限
         */
        DEPT_AND_CHILD,
        /**
         * 仅本人数据权限
         */
        SELF,
        /**
         * 自定义数据权限
         */
        CUSTOM
    }
}

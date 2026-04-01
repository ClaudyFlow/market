package com.market.aspect;

import com.market.annotation.DataScope;
import com.market.annotation.DataScope.ScopeType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据权限切面
 *
 * 拦截带有 @DataScope 注解的方法，自动添加数据权限过滤
 * 将权限过滤参数传递给 Service 层
 */
@Slf4j
@Aspect
@Component
public class DataScopeAspect {

    private static final Logger log = LoggerFactory.getLogger(DataScopeAspect.class);

    /**
     * 线程局部变量，存储当前数据权限范围
     */
    private static final ThreadLocal<DataScopeContext> currentScope = new ThreadLocal<>();

    /**
     * 围绕带有 @DataScope 注解的方法执行
     */
    @Around("@annotation(com.market.annotation.DataScope)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解
        DataScope dataScope = method.getAnnotation(DataScope.class);

        // 构建数据权限上下文
        DataScopeContext context = buildDataScopeContext(dataScope);

        // 设置到线程局部变量
        currentScope.set(context);

        try {
            log.debug("[数据权限] 开始应用数据权限 - 范围：{}", dataScope.scopeType());

            // 执行方法
            return joinPoint.proceed();

        } finally {
            // 清除线程局部变量
            currentScope.remove();
        }
    }

    /**
     * 构建数据权限上下文
     */
    private DataScopeContext buildDataScopeContext(DataScope dataScope) {
        DataScopeContext context = new DataScopeContext();
        context.setScopeType(dataScope.scopeType());
        context.setDeptAlias(dataScope.deptAlias());
        context.setUserAlias(dataScope.userAlias());

        // 模拟当前用户信息（实际应从安全上下文获取）
        context.setCurrentUserId(1L);
        context.setCurrentDeptId(100L);
        context.setCurrentDeptIds(new Long[]{100L, 101L, 102L});

        // 生成 SQL 过滤条件
        String sqlFilter = generateSqlFilter(context);
        context.setSqlFilter(sqlFilter);

        log.debug("[数据权限] SQL 过滤条件：{}", sqlFilter);

        return context;
    }

    /**
     * 生成 SQL 过滤条件
     */
    private String generateSqlFilter(DataScopeContext context) {
        ScopeType scopeType = context.getScopeType();
        String deptAlias = context.getDeptAlias();
        String userAlias = context.getUserAlias();

        // 构建别名
        String deptTableAlias = (deptAlias != null && !deptAlias.isEmpty()) ? deptAlias + "." : "";
        String userTableAlias = (userAlias != null && !userAlias.isEmpty()) ? userAlias + "." : "";

        switch (scopeType) {
            case ALL:
                // 全部数据，不过滤
                return "";

            case SELF:
                // 仅本人数据
                return userTableAlias + "user_id = " + context.getCurrentUserId();

            case DEPT:
                // 本部门数据
                return deptTableAlias + "dept_id = " + context.getCurrentDeptId();

            case DEPT_AND_CHILD:
                // 本部门及子部门数据
                Long[] deptIds = context.getCurrentDeptIds();
                if (deptIds == null || deptIds.length == 0) {
                    return "1=0";
                }
                StringBuilder sb = new StringBuilder();
                sb.append(deptTableAlias).append("dept_id IN (");
                for (int i = 0; i < deptIds.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(deptIds[i]);
                }
                sb.append(")");
                return sb.toString();

            case CUSTOM:
                // 自定义数据权限（从用户关联表中获取）
                return generateCustomFilter(context);

            default:
                return "";
        }
    }

    /**
     * 生成自定义数据权限过滤条件
     */
    private String generateCustomFilter(DataScopeContext context) {
        // 这里可以根据具体的业务需求生成自定义过滤条件
        // 例如：从用户 - 数据权限关联表中查询用户可访问的数据 ID 列表

        // 示例：假设用户只能访问其负责的数据
        return context.getUserAlias() + ".create_by = " + context.getCurrentUserId();
    }

    /**
     * 获取当前数据权限上下文
     */
    public static DataScopeContext getCurrentScope() {
        return currentScope.get();
    }

    /**
     * 清除当前数据权限上下文
     */
    public static void clearCurrentScope() {
        currentScope.remove();
    }

    /**
     * 数据权限上下文
     */
    public static class DataScopeContext {
        /**
         * 权限范围类型
         */
        private ScopeType scopeType;

        /**
         * 部门别名
         */
        private String deptAlias;

        /**
         * 用户别名
         */
        private String userAlias;

        /**
         * 当前用户 ID
         */
        private Long currentUserId;

        /**
         * 当前部门 ID
         */
        private Long currentDeptId;

        /**
         * 当前部门 ID 列表（包括子部门）
         */
        private Long[] currentDeptIds;

        /**
         * SQL 过滤条件
         */
        private String sqlFilter;

        public ScopeType getScopeType() {
            return scopeType;
        }

        public void setScopeType(ScopeType scopeType) {
            this.scopeType = scopeType;
        }

        public String getDeptAlias() {
            return deptAlias;
        }

        public void setDeptAlias(String deptAlias) {
            this.deptAlias = deptAlias;
        }

        public String getUserAlias() {
            return userAlias;
        }

        public void setUserAlias(String userAlias) {
            this.userAlias = userAlias;
        }

        public Long getCurrentUserId() {
            return currentUserId;
        }

        public void setCurrentUserId(Long currentUserId) {
            this.currentUserId = currentUserId;
        }

        public Long getCurrentDeptId() {
            return currentDeptId;
        }

        public void setCurrentDeptId(Long currentDeptId) {
            this.currentDeptId = currentDeptId;
        }

        public Long[] getCurrentDeptIds() {
            return currentDeptIds;
        }

        public void setCurrentDeptIds(Long[] currentDeptIds) {
            this.currentDeptIds = currentDeptIds;
        }

        public String getSqlFilter() {
            return sqlFilter;
        }

        public void setSqlFilter(String sqlFilter) {
            this.sqlFilter = sqlFilter;
        }

        /**
         * 获取扩展参数（用于 MyBatis 等框架）
         */
        public Map<String, Object> getParams() {
            Map<String, Object> params = new HashMap<>();
            params.put("scopeType", scopeType);
            params.put("deptAlias", deptAlias);
            params.put("userAlias", userAlias);
            params.put("currentUserId", currentUserId);
            params.put("currentDeptId", currentDeptId);
            params.put("currentDeptIds", currentDeptIds);
            params.put("sqlFilter", sqlFilter);
            return params;
        }
    }
}

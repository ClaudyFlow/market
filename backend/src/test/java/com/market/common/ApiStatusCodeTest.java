package com.market.common;

/**
 * API 状态码测试类
 * 
 * 测试方法：直接运行 main 方法
 */
public class ApiStatusCodeTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  API 状态码枚举测试");
        System.out.println("========================================\n");

        boolean allPassed = true;

        // 测试 1: 成功状态码
        allPassed &= testSuccessCodes();

        // 测试 2: 处理中状态码
        allPassed &= testProcessingCodes();

        // 测试 3: 失败状态码
        allPassed &= testErrorCodes();

        // 测试 4: 状态码级别
        allPassed &= testLevels();

        // 测试 5: 状态判断方法
        allPassed &= testStatusMethods();

        System.out.println("\n========================================");
        if (allPassed) {
            System.out.println("  ✓ 所有状态码测试通过!");
        } else {
            System.out.println("  ✗ 部分测试失败!");
            System.exit(1);
        }
        System.out.println("========================================");
    }

    /**
     * 测试 1: 成功状态码 (2000-2999)
     */
    private static boolean testSuccessCodes() {
        System.out.println("测试 1: 成功状态码...");
        try {
            // AVAILABLE
            if (ApiStatusCode.AVAILABLE.getCode() != 2000) {
                System.out.println("  ✗ AVAILABLE 代码应为 2000");
                return false;
            }
            if (ApiStatusCode.AVAILABLE.getLevel() != ApiStatusCode.Level.INFO) {
                System.out.println("  ✗ AVAILABLE 级别应为 INFO");
                return false;
            }
            if (!ApiStatusCode.AVAILABLE.isSuccess()) {
                System.out.println("  ✗ AVAILABLE 应为成功状态");
                return false;
            }

            // AVAILABLE_DEGRADED
            if (ApiStatusCode.AVAILABLE_DEGRADED.getCode() != 2001) {
                System.out.println("  ✗ AVAILABLE_DEGRADED 代码应为 2001");
                return false;
            }

            // AVAILABLE_LIMITED
            if (ApiStatusCode.AVAILABLE_LIMITED.getCode() != 2002) {
                System.out.println("  ✗ AVAILABLE_LIMITED 代码应为 2002");
                return false;
            }

            System.out.println("  ✓ 成功状态码：2000, 2001, 2002");
            System.out.println();
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ 测试失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 测试 2: 处理中状态码 (3000-3999)
     */
    private static boolean testProcessingCodes() {
        System.out.println("测试 2: 处理中状态码...");
        try {
            // CHECKING
            if (ApiStatusCode.CHECKING.getCode() != 3000) {
                System.out.println("  ✗ CHECKING 代码应为 3000");
                return false;
            }
            if (!ApiStatusCode.CHECKING.isProcessing()) {
                System.out.println("  ✗ CHECKING 应为处理中状态");
                return false;
            }

            // RETRYING
            if (ApiStatusCode.RETRYING.getCode() != 3100) {
                System.out.println("  ✗ RETRYING 代码应为 3100");
                return false;
            }

            // WAITING_DEPENDENCY
            if (ApiStatusCode.WAITING_DEPENDENCY.getCode() != 3200) {
                System.out.println("  ✗ WAITING_DEPENDENCY 代码应为 3200");
                return false;
            }

            System.out.println("  ✓ 处理中状态码：3000, 3100, 3200");
            System.out.println();
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ 测试失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 测试 3: 失败状态码 (4000-4999)
     */
    private static boolean testErrorCodes() {
        System.out.println("测试 3: 失败状态码...");
        try {
            // UNAVAILABLE
            if (ApiStatusCode.UNAVAILABLE.getCode() != 4000) {
                System.out.println("  ✗ UNAVAILABLE 代码应为 4000");
                return false;
            }
            if (ApiStatusCode.UNAVAILABLE.getLevel() != ApiStatusCode.Level.URGENT) {
                System.out.println("  ✗ UNAVAILABLE 级别应为 URGENT");
                return false;
            }
            if (!ApiStatusCode.UNAVAILABLE.isError()) {
                System.out.println("  ✗ UNAVAILABLE 应为失败状态");
                return false;
            }

            // TIMEOUT
            if (ApiStatusCode.TIMEOUT.getCode() != 4001) {
                System.out.println("  ✗ TIMEOUT 代码应为 4001");
                return false;
            }

            // DEPENDENCY_UNAVAILABLE
            if (ApiStatusCode.DEPENDENCY_UNAVAILABLE.getCode() != 4100) {
                System.out.println("  ✗ DEPENDENCY_UNAVAILABLE 代码应为 4100");
                return false;
            }

            // DATABASE_UNAVAILABLE
            if (ApiStatusCode.DATABASE_UNAVAILABLE.getCode() != 4101) {
                System.out.println("  ✗ DATABASE_UNAVAILABLE 代码应为 4101");
                return false;
            }

            // REDIS_UNAVAILABLE
            if (ApiStatusCode.REDIS_UNAVAILABLE.getCode() != 4102) {
                System.out.println("  ✗ REDIS_UNAVAILABLE 代码应为 4102");
                return false;
            }

            // EXTERNAL_API_UNAVAILABLE
            if (ApiStatusCode.EXTERNAL_API_UNAVAILABLE.getCode() != 4103) {
                System.out.println("  ✗ EXTERNAL_API_UNAVAILABLE 代码应为 4103");
                return false;
            }

            // DETECTOR_ERROR
            if (ApiStatusCode.DETECTOR_ERROR.getCode() != 4200) {
                System.out.println("  ✗ DETECTOR_ERROR 代码应为 4200");
                return false;
            }

            // SERVICE_ERROR
            if (ApiStatusCode.SERVICE_ERROR.getCode() != 4300) {
                System.out.println("  ✗ SERVICE_ERROR 代码应为 4300");
                return false;
            }

            // CONTINUOUS_FAILURE
            if (ApiStatusCode.CONTINUOUS_FAILURE.getCode() != 4400) {
                System.out.println("  ✗ CONTINUOUS_FAILURE 代码应为 4400");
                return false;
            }

            System.out.println("  ✓ 失败状态码：4000, 4001, 4100, 4101, 4102, 4103, 4200, 4300, 4400");
            System.out.println();
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ 测试失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 测试 4: 状态码级别
     */
    private static boolean testLevels() {
        System.out.println("测试 4: 状态码级别...");
        try {
            ApiStatusCode.Level[] levels = ApiStatusCode.Level.values();
            if (levels.length != 3) {
                System.out.println("  ✗ 应有 3 个级别，实际：" + levels.length);
                return false;
            }

            // INFO
            if (ApiStatusCode.Level.INFO.getDescription() == null) {
                System.out.println("  ✗ INFO 描述不能为空");
                return false;
            }

            // WARNING
            if (ApiStatusCode.Level.WARNING.getDescription() == null) {
                System.out.println("  ✗ WARNING 描述不能为空");
                return false;
            }

            // URGENT
            if (ApiStatusCode.Level.URGENT.getDescription() == null) {
                System.out.println("  ✗ URGENT 描述不能为空");
                return false;
            }

            System.out.println("  ✓ 级别定义：INFO, WARNING, URGENT");
            System.out.println();
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ 测试失败：" + e.getMessage());
            return false;
        }
    }

    /**
     * 测试 5: 状态判断方法
     */
    private static boolean testStatusMethods() {
        System.out.println("测试 5: 状态判断方法...");
        try {
            // isSuccess()
            if (!ApiStatusCode.AVAILABLE.isSuccess()) {
                System.out.println("  ✗ AVAILABLE 应为成功");
                return false;
            }
            if (ApiStatusCode.UNAVAILABLE.isSuccess()) {
                System.out.println("  ✗ UNAVAILABLE 不应为成功");
                return false;
            }

            // isProcessing()
            if (!ApiStatusCode.CHECKING.isProcessing()) {
                System.out.println("  ✗ CHECKING 应为处理中");
                return false;
            }
            if (ApiStatusCode.AVAILABLE.isProcessing()) {
                System.out.println("  ✗ AVAILABLE 不应为处理中");
                return false;
            }

            // isError()
            if (!ApiStatusCode.UNAVAILABLE.isError()) {
                System.out.println("  ✗ UNAVAILABLE 应为失败");
                return false;
            }
            if (ApiStatusCode.AVAILABLE.isError()) {
                System.out.println("  ✗ AVAILABLE 不应为失败");
                return false;
            }

            // fromCode()
            ApiStatusCode status = ApiStatusCode.fromCode(2000);
            if (status != ApiStatusCode.AVAILABLE) {
                System.out.println("  ✗ fromCode(2000) 应返回 AVAILABLE");
                return false;
            }

            System.out.println("  ✓ isSuccess(), isProcessing(), isError(), fromCode() 方法正常");
            System.out.println();
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ 测试失败：" + e.getMessage());
            return false;
        }
    }
}

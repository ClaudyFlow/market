package com.market.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API 状态码测试类
 */
class ApiStatusCodeTest {

    @Test
    void testSuccessCodes() {
        // AVAILABLE
        assertEquals(2000, ApiStatusCode.AVAILABLE.getCode(), "AVAILABLE 代码应为 2000");
        assertEquals(ApiStatusCode.Level.INFO, ApiStatusCode.AVAILABLE.getLevel(), "AVAILABLE 级别应为 INFO");
        assertTrue(ApiStatusCode.AVAILABLE.isSuccess(), "AVAILABLE 应为成功状态");

        // AVAILABLE_DEGRADED
        assertEquals(2001, ApiStatusCode.AVAILABLE_DEGRADED.getCode(), "AVAILABLE_DEGRADED 代码应为 2001");

        // AVAILABLE_LIMITED
        assertEquals(2002, ApiStatusCode.AVAILABLE_LIMITED.getCode(), "AVAILABLE_LIMITED 代码应为 2002");
    }

    @Test
    void testProcessingCodes() {
        // CHECKING
        assertEquals(3000, ApiStatusCode.CHECKING.getCode(), "CHECKING 代码应为 3000");
        assertTrue(ApiStatusCode.CHECKING.isProcessing(), "CHECKING 应为处理中状态");

        // RETRYING
        assertEquals(3100, ApiStatusCode.RETRYING.getCode(), "RETRYING 代码应为 3100");

        // WAITING_DEPENDENCY
        assertEquals(3200, ApiStatusCode.WAITING_DEPENDENCY.getCode(), "WAITING_DEPENDENCY 代码应为 3200");
    }

    @Test
    void testErrorCodes() {
        // UNAVAILABLE
        assertEquals(4000, ApiStatusCode.UNAVAILABLE.getCode(), "UNAVAILABLE 代码应为 4000");
        assertEquals(ApiStatusCode.Level.URGENT, ApiStatusCode.UNAVAILABLE.getLevel(), "UNAVAILABLE 级别应为 URGENT");
        assertTrue(ApiStatusCode.UNAVAILABLE.isError(), "UNAVAILABLE 应为失败状态");

        // TIMEOUT
        assertEquals(4001, ApiStatusCode.TIMEOUT.getCode(), "TIMEOUT 代码应为 4001");

        // DEPENDENCY_UNAVAILABLE
        assertEquals(4100, ApiStatusCode.DEPENDENCY_UNAVAILABLE.getCode(), "DEPENDENCY_UNAVAILABLE 代码应为 4100");

        // DATABASE_UNAVAILABLE
        assertEquals(4101, ApiStatusCode.DATABASE_UNAVAILABLE.getCode(), "DATABASE_UNAVAILABLE 代码应为 4101");

        // REDIS_UNAVAILABLE
        assertEquals(4102, ApiStatusCode.REDIS_UNAVAILABLE.getCode(), "REDIS_UNAVAILABLE 代码应为 4102");

        // EXTERNAL_API_UNAVAILABLE
        assertEquals(4103, ApiStatusCode.EXTERNAL_API_UNAVAILABLE.getCode(), "EXTERNAL_API_UNAVAILABLE 代码应为 4103");

        // DETECTOR_ERROR
        assertEquals(4200, ApiStatusCode.DETECTOR_ERROR.getCode(), "DETECTOR_ERROR 代码应为 4200");

        // SERVICE_ERROR
        assertEquals(4300, ApiStatusCode.SERVICE_ERROR.getCode(), "SERVICE_ERROR 代码应为 4300");

        // CONTINUOUS_FAILURE
        assertEquals(4400, ApiStatusCode.CONTINUOUS_FAILURE.getCode(), "CONTINUOUS_FAILURE 代码应为 4400");
    }

    @Test
    void testLevels() {
        ApiStatusCode.Level[] levels = ApiStatusCode.Level.values();
        assertEquals(3, levels.length, "应有 3 个级别");

        // INFO
        assertNotNull(ApiStatusCode.Level.INFO.getDescription(), "INFO 描述不能为空");

        // WARNING
        assertNotNull(ApiStatusCode.Level.WARNING.getDescription(), "WARNING 描述不能为空");

        // URGENT
        assertNotNull(ApiStatusCode.Level.URGENT.getDescription(), "URGENT 描述不能为空");
    }

    @Test
    void testStatusMethods() {
        // isSuccess()
        assertTrue(ApiStatusCode.AVAILABLE.isSuccess(), "AVAILABLE 应为成功");
        assertFalse(ApiStatusCode.UNAVAILABLE.isSuccess(), "UNAVAILABLE 不应为成功");

        // isProcessing()
        assertTrue(ApiStatusCode.CHECKING.isProcessing(), "CHECKING 应为处理中");
        assertFalse(ApiStatusCode.AVAILABLE.isProcessing(), "AVAILABLE 不应为处理中");

        // isError()
        assertTrue(ApiStatusCode.UNAVAILABLE.isError(), "UNAVAILABLE 应为失败");
        assertFalse(ApiStatusCode.AVAILABLE.isError(), "AVAILABLE 不应为失败");

        // fromCode()
        ApiStatusCode status = ApiStatusCode.fromCode(2000);
        assertEquals(ApiStatusCode.AVAILABLE, status, "fromCode(2000) 应返回 AVAILABLE");
    }
}

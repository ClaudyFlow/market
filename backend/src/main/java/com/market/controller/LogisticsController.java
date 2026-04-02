package com.market.controller;

import com.market.annotation.AuditLog;
import com.market.common.Result;
import com.market.entity.LogisticsInfo;
import com.market.entity.LogisticsTrack;
import com.market.entity.User;
import com.market.service.LogisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 物流控制器（预留接口，模拟物流查询）
 */
@RestController
@RequestMapping("/api/logistics")
@CrossOrigin(origins = "*")
public class LogisticsController {

    @Autowired
    private LogisticsService logisticsService;

    /**
     * 获取快递公司列表
     */
    @GetMapping("/companies")
    @AuditLog(module = "物流管理", action = "查询快递公司列表")
    public Result<List<Map<String, String>>> getExpressCompanies() {
        List<Map<String, String>> companies = logisticsService.getExpressCompanies();
        return Result.success(companies);
    }

    /**
     * 查询物流轨迹（根据订单 ID）
     */
    @GetMapping("/order/{orderId}")
    @AuditLog(module = "物流管理", action = "查询订单物流")
    public Result<Map<String, Object>> getLogisticsByOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            LogisticsInfo logistics = logisticsService.getLogisticsByOrderId(orderId);
            List<LogisticsTrack> tracks = logisticsService.getTrackingRecords(logistics.getTrackingNo());

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", logistics.getOrderId());
            result.put("trackingNo", logistics.getTrackingNo());
            result.put("companyCode", logistics.getCompanyCode());
            result.put("companyName", logistics.getCompanyName());
            result.put("status", logistics.getStatus());
            result.put("estimatedDelivery", logistics.getEstimatedDelivery());
            result.put("tracks", convertTracksToMap(tracks));

            return Result.success(result);
        } catch (RuntimeException e) {
            // 如果物流信息不存在，返回空数据（实际对接时可查询第三方物流 API）
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("trackingNo", null);
            result.put("status", "NOT_FOUND");
            result.put("message", "暂无物流信息");
            result.put("tracks", new ArrayList<>());
            return Result.success(result);
        }
    }

    /**
     * 查询物流轨迹（根据运单号）
     */
    @PostMapping("/track")
    @AuditLog(module = "物流管理", action = "查询物流轨迹", recordParams = true)
    public Result<Map<String, Object>> queryTracking(
            @RequestBody Map<String, String> data,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        String trackingNo = data.get("trackingNo");
        String companyCode = data.get("companyCode");

        try {
            List<LogisticsTrack> tracks = logisticsService.getTrackingRecords(trackingNo);
            
            Map<String, Object> result = new HashMap<>();
            result.put("trackingNo", trackingNo);
            result.put("companyCode", companyCode);
            result.put("tracks", convertTracksToMap(tracks));

            return Result.success(result);
        } catch (RuntimeException e) {
            // 返回模拟物流轨迹（用于测试）
            List<LogisticsTrack> mockTracks = logisticsService.generateMockTrackingRecords(trackingNo);
            
            Map<String, Object> result = new HashMap<>();
            result.put("trackingNo", trackingNo);
            result.put("companyCode", companyCode);
            result.put("tracks", convertTracksToMap(mockTracks));
            result.put("mock", true);
            result.put("message", "模拟物流数据");

            return Result.success(result);
        }
    }

    /**
     * 模拟物流轨迹（测试用）
     * 为指定订单生成模拟物流信息
     */
    @PostMapping("/mock-generate/{orderId}")
    @AuditLog(module = "物流管理", action = "生成模拟物流", logLevel = AuditLog.LogLevel.WARNING)
    public Result<Map<String, Object>> generateMockLogistics(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> data) {

        String trackingNo = data.get("trackingNo");
        String companyCode = data.get("companyCode");
        String companyName = data.get("companyName");

        if (trackingNo == null) {
            trackingNo = "YT" + System.currentTimeMillis();
        }
        if (companyCode == null) {
            companyCode = "YTO";
        }
        if (companyName == null) {
            companyName = "圆通速递";
        }

        // 创建物流信息
        logisticsService.createLogistics(orderId, trackingNo, companyCode, companyName);

        // 添加模拟轨迹
        logisticsService.addTrack(trackingNo, "已收件", "广州转运中心", "PICKUP");
        logisticsService.addTrack(trackingNo, "已发出", "深圳分拣中心", "IN_TRANSIT");
        logisticsService.addTrack(trackingNo, "运输中", "北京集散中心", "IN_TRANSIT");
        logisticsService.addTrack(trackingNo, "派送中", "北京市朝阳区", "OUT_FOR_DELIVERY");
        logisticsService.addTrack(trackingNo, "已签收", "用户签收", "DELIVERED");

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("trackingNo", trackingNo);
        result.put("message", "模拟物流信息已生成");

        return Result.success(result);
    }

    /**
     * 物流状态更新回调（预留接口）
     * 实际对接时，物流公司会异步回调此接口
     */
    @PostMapping("/webhook")
    @AuditLog(module = "物流管理", action = "物流状态回调", logLevel = AuditLog.LogLevel.INFO)
    public Result<Map<String, String>> logisticsWebhook(@RequestBody Map<String, String> data) {
        String trackingNo = data.get("trackingNo");
        String status = data.get("status");
        String description = data.get("description");
        String location = data.get("location");

        // 添加物流轨迹
        logisticsService.addTrack(trackingNo, description, location, status);

        Map<String, String> result = new HashMap<>();
        result.put("message", "回调处理成功");
        result.put("trackingNo", trackingNo);

        return Result.success(result);
    }

    /**
     * 转换物流轨迹为 Map
     */
    private List<Map<String, Object>> convertTracksToMap(List<LogisticsTrack> tracks) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (LogisticsTrack track : tracks) {
            Map<String, Object> item = new HashMap<>();
            item.put("time", track.getTime());
            item.put("location", track.getLocation());
            item.put("description", track.getDescription());
            item.put("status", track.getStatus());
            result.add(item);
        }
        return result;
    }
}

package com.market.controller;

import com.market.annotation.AuditLog;
import com.market.annotation.Idempotent;
import com.market.common.Result;
import com.market.entity.Payment;
import com.market.entity.PaymentRefund;
import com.market.entity.User;
import com.market.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 支付控制器
 * 提供支付单创建、支付回调处理、支付状态查询、退款申请与审核等功能（模拟支付流程）。
 * 权限要求：用户端需要登录，退款审核需要商家或管理员角色
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/payment
 */
@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 创建支付单
     * API路径：POST /api/payment/create
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param data 请求数据（包含 orderNo、amount、paymentMethod）
     * @return 支付参数（模拟）
     */
    @PostMapping("/create")
    @Idempotent(key = "'create_payment_' + #user.id + '_' + #data.get(\"orderNo\")", expire = 3600)
    @AuditLog(module = "支付管理", action = "创建支付单", recordParams = true)
    public Result<Map<String, Object>> createPayment(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> data) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        String orderNo = data.get("orderNo").toString();
        BigDecimal amount = new BigDecimal(data.get("amount").toString());
        String paymentMethod = data.get("paymentMethod").toString();

        Payment payment = paymentService.createPayment(user, orderNo, amount, paymentMethod);

        Map<String, Object> paymentParams = new HashMap<>();
        paymentParams.put("paymentNo", payment.getPaymentNo());
        paymentParams.put("orderNo", payment.getOrderNo());
        paymentParams.put("amount", payment.getAmount());
        paymentParams.put("paymentMethod", payment.getPaymentMethod());

        if ("ALIPAY".equals(paymentMethod)) {
            paymentParams.put("alipayUrl", "https://openapi.alipay.com/gateway.do?app_id=xxx");
            paymentParams.put("qrCode", "模拟支付宝二维码数据");
        } else if ("WECHAT".equals(paymentMethod)) {
            paymentParams.put("wechatCodeUrl", "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi");
            paymentParams.put("prepayId", "模拟微信预支付 ID");
        } else if ("BANK".equals(paymentMethod)) {
            paymentParams.put("bankCode", "模拟银行代码");
            paymentParams.put("bankUrl", "https://payment.bank.com/gateway");
        }

        return Result.success(paymentParams);
    }

    /**
     * 模拟支付回调
     * API路径：POST /api/payment/callback/{method}
     * 权限：公开（实际对接时由支付平台回调）
     *
     * @param method 支付方式（ALIPAY/WECHAT等）
     * @param callbackData 回调数据（包含 paymentNo、status、transactionId）
     * @return 回调处理结果
     */
    @PostMapping("/callback/{method}")
    @AuditLog(module = "支付管理", action = "支付回调", logLevel = AuditLog.LogLevel.INFO)
    public Result<Map<String, String>> paymentCallback(
            @PathVariable String method,
            @RequestBody Map<String, String> callbackData) {

        String paymentNo = callbackData.get("paymentNo");
        String status = callbackData.get("status");
        String transactionId = callbackData.get("transactionId");

        if (transactionId == null) {
            transactionId = "TXN" + System.currentTimeMillis();
        }

        Payment payment;
        if ("SUCCESS".equals(status)) {
            payment = paymentService.simulatePaymentSuccess(paymentNo, transactionId);
        } else {
            payment = paymentService.simulatePaymentFailed(paymentNo, "支付失败");
        }

        Map<String, String> result = new HashMap<>();
        result.put("paymentNo", payment.getPaymentNo());
        result.put("status", payment.getStatus());
        result.put("message", "回调处理成功");

        if ("ALIPAY".equals(method)) {
            result.put("return_code", "success");
        } else if ("WECHAT".equals(method)) {
            result.put("return_code", "SUCCESS");
        }

        return Result.success(result);
    }

    /**
     * 查询支付状态
     * API路径：GET /api/payment/status/{paymentNo}
     * 权限：公开
     *
     * @param paymentNo 支付单号
     * @return 支付状态信息
     */
    @GetMapping("/status/{paymentNo}")
    @AuditLog(module = "支付管理", action = "查询支付状态")
    public Result<Map<String, Object>> getPaymentStatus(@PathVariable String paymentNo) {
        Payment payment = paymentService.getPaymentByPaymentNo(paymentNo);

        Map<String, Object> result = new HashMap<>();
        result.put("paymentNo", payment.getPaymentNo());
        result.put("orderNo", payment.getOrderNo());
        result.put("amount", payment.getAmount());
        result.put("status", payment.getStatus());
        result.put("paymentMethod", payment.getPaymentMethod());
        result.put("paidAt", payment.getPaidAt());
        result.put("transactionId", payment.getTransactionId());

        return Result.success(result);
    }

    /**
     * 根据订单号查询支付单
     * API路径：GET /api/payment/order/{orderNo}
     * 权限：公开
     *
     * @param orderNo 订单号
     * @return 支付单信息
     */
    @GetMapping("/order/{orderNo}")
    @AuditLog(module = "支付管理", action = "查询订单支付单")
    public Result<Map<String, Object>> getPaymentByOrderNo(@PathVariable String orderNo) {
        Payment payment = paymentService.getPaymentByOrderNo(orderNo);

        if (payment == null) {
            return Result.error(404, "支付单不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("paymentNo", payment.getPaymentNo());
        result.put("orderNo", payment.getOrderNo());
        result.put("amount", payment.getAmount());
        result.put("status", payment.getStatus());
        result.put("paymentMethod", payment.getPaymentMethod());
        result.put("paidAt", payment.getPaidAt());

        return Result.success(result);
    }

    /**
     * 申请退款
     * API路径：POST /api/payment/refund
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param data 退款请求数据（包含 orderId、paymentNo、amount、reason、images）
     * @return 退款申请结果
     */
    @PostMapping("/refund")
    @Idempotent(key = "'apply_refund_' + #user.id + '_' + #data.get(\"orderId\")", expire = 3600)
    @AuditLog(module = "支付管理", action = "申请退款", recordParams = true)
    public Result<Map<String, Object>> applyRefund(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> data) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Long orderId = Long.valueOf(data.get("orderId").toString());
        String paymentNo = data.get("paymentNo").toString();
        BigDecimal amount = new BigDecimal(data.get("amount").toString());
        String reason = data.get("reason").toString();
        @SuppressWarnings("unchecked")
        List<String> images = (List<String>) data.get("images");

        PaymentRefund refund = paymentService.createRefund(user, orderId, paymentNo, amount, reason, images);

        Map<String, Object> result = new HashMap<>();
        result.put("refundNo", refund.getRefundNo());
        result.put("status", refund.getStatus());
        result.put("amount", refund.getAmount());
        result.put("message", "退款申请已提交，等待商家审核");

        return Result.success(result);
    }

    /**
     * 查询退款状态
     * API路径：GET /api/payment/refund/status/{refundNo}
     * 权限：公开
     *
     * @param refundNo 退款单号
     * @return 退款状态信息
     */
    @GetMapping("/refund/status/{refundNo}")
    @AuditLog(module = "支付管理", action = "查询退款状态")
    public Result<Map<String, Object>> getRefundStatus(@PathVariable String refundNo) {
        PaymentRefund refund = paymentService.getRefundByRefundNo(refundNo);

        Map<String, Object> result = new HashMap<>();
        result.put("refundNo", refund.getRefundNo());
        result.put("paymentNo", refund.getPaymentNo());
        result.put("amount", refund.getAmount());
        result.put("status", refund.getStatus());
        result.put("reason", refund.getReason());
        result.put("merchantRemark", refund.getMerchantRemark());
        result.put("createdAt", refund.getCreatedAt());
        result.put("refundedAt", refund.getRefundedAt());

        return Result.success(result);
    }

    /**
     * 获取用户退款列表
     * API路径：GET /api/payment/refund/list
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 用户退款列表
     */
    @GetMapping("/refund/list")
    @AuditLog(module = "支付管理", action = "查询用户退款列表")
    public Result<List<Map<String, Object>>> getUserRefunds(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }

        List<PaymentRefund> refunds = paymentService.getUserRefunds(user.getId());
        List<Map<String, Object>> result = new ArrayList<>();

        for (PaymentRefund refund : refunds) {
            Map<String, Object> item = new HashMap<>();
            item.put("refundNo", refund.getRefundNo());
            item.put("orderId", refund.getOrderId());
            item.put("amount", refund.getAmount());
            item.put("status", refund.getStatus());
            item.put("reason", refund.getReason());
            item.put("createdAt", refund.getCreatedAt());
            result.add(item);
        }

        return Result.success(result);
    }

    /**
     * 审核退款（商家/管理员）
     * API路径：POST /api/payment/refund/approve
     * 权限：需要商家或管理员角色
     *
     * @param user 当前登录用户（商家或管理员）
     * @param data 审核数据（包含 refundNo、approved、remark）
     * @return 审核结果
     */
    @PostMapping("/refund/approve")
    @Idempotent(key = "'approve_refund_' + #data.get(\"refundNo\")", expire = 600)
    @AuditLog(module = "支付管理", action = "审核退款", recordParams = true)
    public Result<Map<String, String>> approveRefund(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> data) {

        if (user == null || (!"MERCHANT".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
            return Result.error(403, "无权操作");
        }

        String refundNo = data.get("refundNo").toString();
        boolean approved = Boolean.parseBoolean(data.get("approved").toString());
        String remark = data.get("remark") != null ? data.get("remark").toString() : "";

        paymentService.approveRefund(refundNo, approved, remark);

        Map<String, String> result = new HashMap<>();
        result.put("message", approved ? "退款已同意" : "退款已拒绝");
        return Result.success(result);
    }

    /**
     * 模拟支付（测试用）
     * API路径：POST /api/payment/mock-pay/{paymentNo}
     * 权限：公开（仅用于测试）
     *
     * @param paymentNo 支付单号
     * @return 模拟支付结果
     */
    @PostMapping("/mock-pay/{paymentNo}")
    @AuditLog(module = "支付管理", action = "模拟支付", logLevel = AuditLog.LogLevel.WARNING)
    public Result<Map<String, Object>> mockPayment(@PathVariable String paymentNo) {
        String transactionId = "MOCK" + System.currentTimeMillis();
        Payment payment = paymentService.simulatePaymentSuccess(paymentNo, transactionId);

        Map<String, Object> result = new HashMap<>();
        result.put("paymentNo", payment.getPaymentNo());
        result.put("status", payment.getStatus());
        result.put("transactionId", payment.getTransactionId());
        result.put("paidAt", payment.getPaidAt());
        result.put("message", "模拟支付成功");

        return Result.success(result);
    }
}

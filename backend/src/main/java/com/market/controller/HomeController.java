package com.market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页控制器 - 提供 API 欢迎信息
 */
@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public Map<String, Object> index() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "欢迎使用市场平台 API");
        result.put("version", "1.0.0");
        result.put("endpoints", new String[]{
            "/api/auth - 认证接口",
            "/api/product - 商品接口",
            "/api/cart - 购物车接口",
            "/api/order - 订单接口",
            "/api/user - 用户接口"
        });
        return result;
    }

    @GetMapping("/api")
    @ResponseBody
    public Map<String, Object> apiInfo() {
        return index();
    }
}

package com.market.aspect;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.market.annotation.SensitiveData;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 敏感数据脱敏处理
 *
 * 通过 ResponseBodyAdvice 拦截返回结果，对带有 @SensitiveData 注解的字段进行脱敏
 */
@Slf4j
@Aspect
@RestControllerAdvice
public class SensitiveDataAspect implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(SensitiveDataAspect.class);

    /**
     * 手机号脱敏规则：前 3 位 + 4 个* + 后 4 位
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\d{3})\\d{4}(\\d{4})");

    /**
     * 身份证脱敏规则：前 6 位 + 8 个* + 后 4 位
     */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(\\d{6})\\d{8}(\\w{4})");

    /**
     * 银行卡脱敏规则：前 4 位 + 4 个* + 后 4 位
     */
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("(\\d{4})\\d+(\\d{4})");

    /**
     * 邮箱脱敏规则：前 3 位 + * + @ + 域名
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("(^\\w{3})\\w+(@\\w+\\.[a-z]+$)");

    /**
     * 是否支持
     */
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        // 只处理 JSON 响应
        return returnType.getMethod() != null;
    }

    /**
     * 处理返回体
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                   MethodParameter returnType,
                                   MediaType selectedContentType,
                                   Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                   ServerHttpRequest request,
                                   ServerHttpResponse response) {
        if (body == null) {
            return null;
        }

        try {
            // 检查是否需要脱敏
            if (shouldDesensitize(returnType)) {
                log.debug("[敏感数据] 开始脱敏处理");
                return desensitize(body);
            }
        } catch (Exception e) {
            log.error("[敏感数据] 脱敏失败", e);
        }

        return body;
    }

    /**
     * 检查是否需要脱敏
     */
    private boolean shouldDesensitize(MethodParameter returnType) {
        // 检查方法返回值类型是否有注解
        if (returnType.getMethodAnnotation(SensitiveData.class) != null) {
            return true;
        }

        // 检查返回值类型的字段
        Class<?> parameterType = returnType.getParameterType();
        for (Field field : getAllFields(parameterType)) {
            if (field.isAnnotationPresent(SensitiveData.class)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 脱敏处理
     */
    private Object desensitize(Object obj) {
        if (obj == null) {
            return null;
        }

        Class<?> clazz = obj.getClass();

        // 如果是基本类型或 String，直接处理
        if (obj instanceof String) {
            SensitiveData annotation = clazz.getAnnotation(SensitiveData.class);
            if (annotation != null) {
                return desensitizeValue((String) obj, annotation);
            }
            return obj;
        }

        // 处理对象字段
        try {
            for (Field field : getAllFields(clazz)) {
                SensitiveData annotation = field.getAnnotation(SensitiveData.class);
                if (annotation != null) {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (value instanceof String) {
                        String desensitizedValue = desensitizeValue((String) value, annotation);
                        field.set(obj, desensitizedValue);
                    }
                }
            }
        } catch (Exception e) {
            log.error("[敏感数据] 字段脱敏失败", e);
        }

        return obj;
    }

    /**
     * 根据类型脱敏
     */
    private String desensitizeValue(String value, SensitiveData annotation) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        SensitiveData.SensitiveType type = annotation.type();
        char maskChar = annotation.maskChar();
        int prefixLength = annotation.prefixLength();
        int suffixLength = annotation.suffixLength();

        // 自定义脱敏
        if (type == SensitiveData.SensitiveType.CUSTOM) {
            return customDesensitize(value, maskChar, prefixLength, suffixLength);
        }

        // 根据类型脱敏
        switch (type) {
            case PHONE:
                return desensitizePhone(value);
            case ID_CARD:
                return desensitizeIdCard(value);
            case BANK_CARD:
                return desensitizeBankCard(value);
            case EMAIL:
                return desensitizeEmail(value);
            case ADDRESS:
                return desensitizeAddress(value);
            case NAME:
                return desensitizeName(value);
            case PASSWORD:
                return desensitizePassword(value);
            default:
                return value;
        }
    }

    /**
     * 手机号脱敏
     */
    private String desensitizePhone(String phone) {
        Matcher matcher = PHONE_PATTERN.matcher(phone);
        if (matcher.matches()) {
            return matcher.group(1) + "****" + matcher.group(2);
        }
        return phone;
    }

    /**
     * 身份证脱敏
     */
    private String desensitizeIdCard(String idCard) {
        Matcher matcher = ID_CARD_PATTERN.matcher(idCard);
        if (matcher.matches()) {
            return matcher.group(1) + "********" + matcher.group(2);
        }
        return idCard;
    }

    /**
     * 银行卡脱敏
     */
    private String desensitizeBankCard(String bankCard) {
        Matcher matcher = BANK_CARD_PATTERN.matcher(bankCard);
        if (matcher.matches()) {
            return matcher.group(1) + "****" + matcher.group(2);
        }
        return bankCard;
    }

    /**
     * 邮箱脱敏
     */
    private String desensitizeEmail(String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        if (matcher.matches()) {
            return matcher.group(1) + "****" + matcher.group(2);
        }
        return email;
    }

    /**
     * 地址脱敏
     */
    private String desensitizeAddress(String address) {
        if (address.length() <= 4) {
            return address;
        }
        return address.substring(0, 4) + "****";
    }

    /**
     * 姓名脱敏
     */
    private String desensitizeName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        if (name.length() == 1) {
            return "*";
        }
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "**";
    }

    /**
     * 密码脱敏（全部用*）
     */
    private String desensitizePassword(String password) {
        if (password == null || password.isEmpty()) {
            return password;
        }
        return "*".repeat(password.length());
    }

    /**
     * 自定义脱敏
     */
    private String customDesensitize(String value, char maskChar, int prefixLength, int suffixLength) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        int length = value.length();
        if (length <= prefixLength + suffixLength) {
            return String.valueOf(maskChar).repeat(length);
        }

        StringBuilder sb = new StringBuilder();
        if (prefixLength > 0) {
            sb.append(value, 0, prefixLength);
        }
        sb.append(String.valueOf(maskChar).repeat(length - prefixLength - suffixLength));
        if (suffixLength > 0) {
            sb.append(value, length - suffixLength, length);
        }

        return sb.toString();
    }

    /**
     * 获取所有字段（包括父类）
     */
    private Field[] getAllFields(Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return new Field[0];
        }

        Field[] fields = clazz.getDeclaredFields();
        Field[] parentFields = getAllFields(clazz.getSuperclass());

        Field[] allFields = new Field[fields.length + parentFields.length];
        System.arraycopy(fields, 0, allFields, 0, fields.length);
        System.arraycopy(parentFields, 0, allFields, fields.length, parentFields.length);

        return allFields;
    }
}

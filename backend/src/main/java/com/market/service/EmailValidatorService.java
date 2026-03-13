package com.market.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Hashtable;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

@Service
public class EmailValidatorService {

    private static final Logger log = LoggerFactory.getLogger(EmailValidatorService.class);

    // RFC 5322标准邮箱正则表达式
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

    /**
     * 基础邮箱格式验证
     */
    public boolean isValidEmailFormat(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        // 使用正则表达式验证
        if (!email.matches(EMAIL_REGEX)) {
            return false;
        }

        return true;
    }

    /**
     * 验证邮箱域名的MX记录
     */
    public boolean hasMXRecord(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String domain = extractDomain(email);
        if (domain == null) {
            return false;
        }

        try {
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");

            DirContext ctx = new InitialDirContext(env);
            Attributes attrs = ctx.getAttributes(domain, new String[]{"MX"});
            Attribute mxRecords = attrs.get("MX");

            if (mxRecords != null && mxRecords.size() > 0) {
                log.info("域名 {} 有有效的MX记录", domain);
                ctx.close();
                return true;
            }

            ctx.close();
            log.warn("域名 {} 没有MX记录", domain);
            return false;
        } catch (NamingException e) {
            log.warn("无法验证域名 {} 的MX记录: {}", domain, e.getMessage());
            // 如果DNS查询失败，我们仍然允许邮箱通过（可能是网络问题）
            return true;
        }
    }

    /**
     * 提取邮箱域名
     */
    private String extractDomain(String email) {
        int atIndex = email.lastIndexOf('@');
        if (atIndex == -1 || atIndex == email.length() - 1) {
            return null;
        }
        return email.substring(atIndex + 1);
    }

    /**
     * 完整的邮箱验证（格式+MX记录）
     */
    public boolean isValidEmail(String email) {
        if (!isValidEmailFormat(email)) {
            log.warn("邮箱格式无效: {}", email);
            return false;
        }

        if (!hasMXRecord(email)) {
            log.warn("邮箱域名无效: {}", email);
            return false;
        }

        return true;
    }

    /**
     * 获取邮箱验证错误信息
     */
    public String getEmailValidationError(String email) {
        if (email == null || email.isEmpty()) {
            return "邮箱不能为空";
        }

        if (!isValidEmailFormat(email)) {
            return "请输入正确的邮箱地址";
        }

        if (!hasMXRecord(email)) {
            return "邮箱域名不存在，请输入正确的邮箱地址";
        }

        return "";
    }
}
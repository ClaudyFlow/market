package com.market.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Value("${verification.code.length:6}")
    private int codeLength;

    @Value("${verification.code.expire.minutes:5}")
    private int codeExpireMinutes;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 生成随机验证码
     */
    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 发送验证码邮件
     */
    public boolean sendVerificationCode(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Market 验证码");
            message.setText("您的验证码是：" + code + "，有效期为" + codeExpireMinutes + "分钟。\n\n" +
                           "如果这不是您本人操作，请忽略此邮件。");

            mailSender.send(message);
            log.info("验证码已发送到邮箱: {}", email);
            return true;
        } catch (Exception e) {
            log.error("发送验证码失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 发送HTML格式邮件
     */
    public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("HTML邮件已发送到: {}", to);
            return true;
        } catch (MessagingException e) {
            log.error("发送HTML邮件失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 发送欢迎邮件
     */
    public boolean sendWelcomeEmail(String email, String username) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("欢迎注册 Market！");
            message.setText("亲爱的 " + username + "：\n\n" +
                           "欢迎加入 Market！\n" +
                           "您的账号已成功注册。\n\n" +
                           "如有任何问题，请联系我们的客服团队。\n\n" +
                           "祝您购物愉快！\n" +
                           "Market 团队");

            mailSender.send(message);
            log.info("欢迎邮件已发送到: {}", email);
            return true;
        } catch (Exception e) {
            log.error("发送欢迎邮件失败: {}", e.getMessage());
            return false;
        }
    }
}
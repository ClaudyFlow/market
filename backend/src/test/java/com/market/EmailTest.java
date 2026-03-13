package com.market;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
public class EmailTest {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Test
    public void testSendEmail() {
        if (mailSender == null) {
            System.out.println("MailSender未注入，无法测试邮件发送");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Market <noreply@market.com>");
            message.setTo("2481036245@qq.com");
            message.setSubject("测试验证码邮件");
            String code = String.format("%06d", (int)(Math.random() * 1000000));
            message.setText("您的验证码是：" + code + "\n\n该验证码5分钟内有效，请勿泄露给他人。");

            mailSender.send(message);
            System.out.println("邮件发送成功！验证码: " + code);

        } catch (Exception e) {
            System.out.println("邮件发送失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
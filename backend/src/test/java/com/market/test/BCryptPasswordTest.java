package com.market.test;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BCryptPasswordTest {
    
    @Test
    public void testAdminPassword() {
        // 数据库中的哈希值
        String storedHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8IjtVzXmhqZz.KcXJnZbZfUW";
        String plainPassword = "admin123";
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(plainPassword, storedHash);
        System.out.println("Password matches: " + matches);
        assertTrue(matches, "密码应该匹配");
    }
}

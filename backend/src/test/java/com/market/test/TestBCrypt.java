package com.market.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
public class TestBCrypt {
    public static void main(String[] args) {
        // 数据库中的哈希值
        String storedHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8IjtVzXmhqZz.KcXJnZbZfUW";
        String plainPassword = "admin123";
        
        // 使用 BCrypt 验证
        boolean matches = BCrypt.checkpw(plainPassword, storedHash);
        System.out.println("BCrypt.checkpw result: " + matches);
        
        // 使用 BCryptPasswordEncoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches2 = encoder.matches(plainPassword, storedHash);
        System.out.println("BCryptPasswordEncoder.matches result: " + matches2);
        
        // 生成一个新哈希对比
        String newHash = encoder.encode(plainPassword);
        System.out.println("New encoded hash: " + newHash);
        System.out.println("Matches new hash: " + encoder.matches(plainPassword, newHash));
    }
}

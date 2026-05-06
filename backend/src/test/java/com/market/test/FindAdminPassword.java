package com.market.test;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

public class FindAdminPassword {
    
    @Test
    public void findPassword() {
        String storedHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8IjtVzXmhqZz.KcXJnZbZfUW";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 常见管理员密码
        String[] candidates = {
            "admin", "admin123", "Admin@123", "Admin123", "ADMIN", "ADMIN123",
            "123456", "password", "root", "system", "marketadmin",
            "admin123456", "adminadmin", "admin!123", "Admin@admin",
            "test", "test123", "Demo123!", "demo",
            "qwerty", "111111", "000000"
        };
        
        System.out.println("Testing common passwords against stored hash:");
        for (String candidate : candidates) {
            boolean matches = encoder.matches(candidate, storedHash);
            if (matches) {
                System.out.println("FOUND! Password is: " + candidate);
                return;
            }
        }
        System.out.println("Password not found in common list.");
        
        // 打印新哈希供参考
        System.out.println("\nGenerated hashes for potential passwords:");
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("admin: " + encoder.encode("admin"));
    }
}

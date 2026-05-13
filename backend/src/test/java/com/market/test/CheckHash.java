package com.market.test;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;

public class CheckHash {
    
    @Test
    public void checkPassword() {
        String storedHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8IjtVzXmhqZz.KcXJnZbZfUW";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String[] candidates = {"admin", "admin123", "Admin@123", "Admin123", "123456", "password", "Admin123!"};
        
        for (String candidate : candidates) {
            boolean matches = encoder.matches(candidate, storedHash);
            System.out.println("'" + candidate + "' matches: " + matches);
            if (matches) {
                System.out.println("FOUND! Password is: " + candidate);
            }
        }
        
        // 打印一些新生成的哈希
        System.out.println("\nSample hashes:");
        System.out.println("admin: " + encoder.encode("admin"));
        System.out.println("admin123: " + encoder.encode("admin123"));
    }
}

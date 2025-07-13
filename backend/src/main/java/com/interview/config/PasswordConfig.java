package com.interview.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Configuration
public class PasswordConfig {
    
    @Bean
    public SimplePasswordEncoder passwordEncoder() {
        return new SimplePasswordEncoder();
    }
    
    /**
     * 简单的密码加密实现
     */
    public static class SimplePasswordEncoder {
        
        private static final SecureRandom RANDOM = new SecureRandom();
        
        /**
         * 加密密码
         */
        public String encode(String rawPassword) {
            try {
                // 生成盐值
                byte[] salt = new byte[16];
                RANDOM.nextBytes(salt);
                
                // 使用SHA-256加密
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(salt);
                byte[] hashedPassword = md.digest(rawPassword.getBytes());
                
                // 组合盐值和哈希值
                byte[] combined = new byte[salt.length + hashedPassword.length];
                System.arraycopy(salt, 0, combined, 0, salt.length);
                System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
                
                return Base64.getEncoder().encodeToString(combined);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("密码加密失败", e);
            }
        }
        
        /**
         * 验证密码
         */
        public boolean matches(String rawPassword, String encodedPassword) {
            try {
                // 解码存储的密码
                byte[] combined = Base64.getDecoder().decode(encodedPassword);
                
                // 提取盐值
                byte[] salt = new byte[16];
                System.arraycopy(combined, 0, salt, 0, salt.length);
                
                // 使用相同的盐值加密输入的密码
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(salt);
                byte[] hashedPassword = md.digest(rawPassword.getBytes());
                
                // 比较哈希值
                byte[] storedHash = new byte[combined.length - salt.length];
                System.arraycopy(combined, salt.length, storedHash, 0, storedHash.length);
                
                return MessageDigest.isEqual(hashedPassword, storedHash);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("密码验证失败", e);
            }
        }
    }
} 
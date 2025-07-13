package com.interview.config;

import com.interview.entity.User;
import com.interview.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import com.interview.config.PasswordConfig.SimplePasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimplePasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有用户数据
        if (userRepository.count() == 0) {
            // 创建测试用户
            createTestUsers();
        }
    }

    private void createTestUsers() {
        // 创建测试用户
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setEmail("admin@example.com");
        admin.setFullName("系统管理员");
        admin.setIsActive(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(admin);

        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEmail("test@example.com");
        testUser.setFullName("测试用户");
        testUser.setIsActive(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(testUser);

        User zhangsan = new User();
        zhangsan.setUsername("zhangsan");
        zhangsan.setPassword(passwordEncoder.encode("password"));
        zhangsan.setEmail("zhangsan@example.com");
        zhangsan.setFullName("张三");
        zhangsan.setIsActive(true);
        zhangsan.setCreatedAt(LocalDateTime.now());
        zhangsan.setUpdatedAt(LocalDateTime.now());
        userRepository.save(zhangsan);

        User lisi = new User();
        lisi.setUsername("lisi");
        lisi.setPassword(passwordEncoder.encode("password"));
        lisi.setEmail("lisi@example.com");
        lisi.setFullName("李四");
        lisi.setIsActive(true);
        lisi.setCreatedAt(LocalDateTime.now());
        lisi.setUpdatedAt(LocalDateTime.now());
        userRepository.save(lisi);

        User wangwu = new User();
        wangwu.setUsername("wangwu");
        wangwu.setPassword(passwordEncoder.encode("password"));
        wangwu.setEmail("wangwu@example.com");
        wangwu.setFullName("王五");
        wangwu.setIsActive(true);
        wangwu.setCreatedAt(LocalDateTime.now());
        wangwu.setUpdatedAt(LocalDateTime.now());
        userRepository.save(wangwu);

        System.out.println("测试用户数据初始化完成！");
        System.out.println("可用测试账号：");
        System.out.println("- admin/password");
        System.out.println("- testuser/password");
        System.out.println("- zhangsan/password");
        System.out.println("- lisi/password");
        System.out.println("- wangwu/password");
    }
} 
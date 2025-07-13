package com.interview.controller;

import com.interview.entity.User;
import com.interview.service.UserService;
import com.interview.model.LoginRequest;
import com.interview.model.RegisterRequest;
import com.interview.model.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.authenticateUser(request.getUsername(), request.getPassword());
            if (user != null) {
                LoginResponse response = new LoginResponse();
                response.setSuccess(true);
                response.setMessage("登录成功");
                response.setUser(user);
                return ResponseEntity.ok(response);
            } else {
                LoginResponse response = new LoginResponse();
                response.setSuccess(false);
                response.setMessage("用户名或密码错误");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("登录失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            User newUser = userService.registerUser(
                request.getUsername(), 
                request.getPassword(), 
                request.getEmail(),
                request.getFullName()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("user", newUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "注册失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, Object>> checkAuth(@RequestParam String username) {
        try {
            User user = userService.findByUsername(username);
            Map<String, Object> response = new HashMap<>();
            if (user != null) {
                response.put("authenticated", true);
                response.put("user", user);
            } else {
                response.put("authenticated", false);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("authenticated", false);
            response.put("error", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
} 
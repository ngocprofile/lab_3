package com.example.schoolmanager.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.schoolmanager.model.Role;
import com.example.schoolmanager.model.User;
import com.example.schoolmanager.respository.UserRepository;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    // --- 1. API ĐĂNG KÝ (Register) ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        // Kiểm tra username đã tồn tại chưa
        if (repo.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username đã tồn tại!");
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(encoder.encode(request.getPassword()));

        // Xử lý Role (Mặc định là USER nếu không gửi lên)
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        } else {
            user.setRole(Role.ROLE_USER); // Giả sử bạn có enum Role.USER
        }

        repo.save(user);

        return ResponseEntity.ok("Đăng ký thành công user: " + request.getUsername());
    }

    // --- 2. API ĐĂNG NHẬP (Login) ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Xác thực bằng AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            // Lưu thông tin vào Context (quan trọng cho Security)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lấy thông tin user từ DB để trả về cho Frontend
            User user = repo.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Trả về JSON (Map)
            return ResponseEntity.ok(Map.of(
                    "message", "Login success",
                    "username", user.getUsername(),
                    "role", user.getRole(),
                    "status", "success"));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Sai tài khoản hoặc mật khẩu!");
        }
    }

    // ==========================================
    // CÁC CLASS DTO (Data Transfer Object)
    // Để hứng dữ liệu JSON từ Frontend gửi lên
    // ==========================================

    // Class hứng dữ liệu Đăng ký
    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private Role role;

        // Getters & Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }
    }

    // Class hứng dữ liệu Đăng nhập
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters & Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
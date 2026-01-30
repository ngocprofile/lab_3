package com.example.schoolmanager.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.schoolmanager.model.Role;
import com.example.schoolmanager.model.User;
import com.example.schoolmanager.respository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    // Inject Bean xử lý lưu Session
    @Autowired
    private SecurityContextRepository securityContextRepository;

    // --- LOGIN ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        try {
            // 1. Xác thực Username/Password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            // 2. Tạo Security Context mới
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            // 3. LƯU CONTEXT VÀO SESSION (Bước quan trọng nhất để trình duyệt nhớ login)
            securityContextRepository.saveContext(context, httpRequest, httpResponse);

            // 4. Lấy thông tin User trả về frontend
            User user = repo.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(Map.of(
                    "message", "Login success",
                    "username", user.getUsername(),
                    "role", user.getRole(),
                    "status", "success"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Sai tài khoản hoặc mật khẩu"));
        }
    }

    // --- REGISTER ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (repo.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username đã tồn tại!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        // Xử lý Role an toàn
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        } else {
            user.setRole(Role.ROLE_USER);
        }

        repo.save(user);
        return ResponseEntity.ok("Đăng ký thành công user: " + request.getUsername());
    }

    // --- DTO Classes ---
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters Setters
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

    public static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private Role role;

        // Getters Setters
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
}
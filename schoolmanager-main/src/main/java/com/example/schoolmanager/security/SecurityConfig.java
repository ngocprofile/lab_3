package com.example.schoolmanager.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Cấu hình CORS (Thêm mới đoạn này để sửa lỗi Frontend)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Tắt CSRF (Thường tắt khi làm API cho Mobile/Web SPA)
                .csrf(csrf -> csrf.disable())

                // 3. Phân quyền API
                .authorizeHttpRequests(auth -> auth
                        // API Auth cho phép tất cả truy cập để đăng nhập/đăng ký
                        .requestMatchers("/api/auth/**").permitAll()

                        // GET Students: User hoặc Admin đều xem được
                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyRole("USER", "ADMIN")

                        // POST Students: Chỉ Admin mới được thêm
                        .requestMatchers(HttpMethod.POST, "/api/students/**").hasRole("ADMIN")

                        // Các request còn lại bắt buộc phải đăng nhập
                        .anyRequest().authenticated())

                // 4. Sử dụng Basic Auth (Tạm thời để test, sau này sẽ thay bằng JWT Filter)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    // --- CẤU HÌNH CHI TIẾT CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép frontend chạy ở cổng 5500 gọi vào
        configuration.setAllowedOrigins(List.of("http://127.0.0.1:5500"));

        // Cho phép các method
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cho phép gửi kèm các header (như Authorization, Content-Type)
        configuration.setAllowedHeaders(List.of("*"));

        // Cho phép gửi cookie/credentials (nếu cần)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
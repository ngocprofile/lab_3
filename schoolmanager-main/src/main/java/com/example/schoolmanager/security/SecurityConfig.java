package com.example.schoolmanager.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để dễ test với Postman/JS
                .authorizeHttpRequests(auth -> auth
                        // 1. Cho phép truy cập tài nguyên tĩnh và các trang công khai
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                        .requestMatchers("/login", "/register", "/error").permitAll()

                        // 2. Cho phép API Auth hoạt động tự do (để AuthController tự xử lý)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 3. Phân quyền API nghiệp vụ
                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/students/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/students/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/students/**").hasRole("ADMIN")

                        // 4. Các yêu cầu còn lại bắt buộc phải đăng nhập
                        .anyRequest().authenticated())
                // 5. Cấu hình Form Login chỉ để điều hướng (Redirect) khi chưa login
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        return http.build();
    }

    // Bean quan trọng để lưu Session thủ công trong AuthController
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cho phép cả Localhost và Render
        configuration.setAllowedOrigins(List.of(
                "http://127.0.0.1:5500",
                "http://localhost:5500",
                "https://lab-3-6uc5.onrender.com" // Thay bằng domain render của bạn
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
package com.example.schoolmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // Lưu ý: Dùng @Controller (trả về HTML), KHÔNG dùng @RestController (trả về
            // JSON)
public class ViewController {

    /**
     * Đường dẫn: /login
     * Mục đích: Hiển thị trang đăng nhập
     * File trả về: src/main/resources/templates/login.html
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Đường dẫn: /
     * Mục đích: Trang chủ sau khi đăng nhập thành công
     * File trả về: src/main/resources/templates/student.html
     */
    @GetMapping("/")
    public String homePage() {
        return "student";
    }

    /**
     * Đường dẫn: /student (Dự phòng nếu bạn muốn truy cập rõ ràng)
     */
    @GetMapping("/student")
    public String studentPage() {
        return "student";
    }
}
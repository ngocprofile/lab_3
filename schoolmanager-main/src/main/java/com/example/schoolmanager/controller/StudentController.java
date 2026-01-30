package com.example.schoolmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.schoolmanager.model.Student;
import com.example.schoolmanager.service.StudentService;

@RestController
@RequestMapping("/api/students")
// @CrossOrigin -> Đã bỏ dòng này vì đã cấu hình Global CORS bên SecurityConfig
public class StudentController {

    @Autowired
    private StudentService service;

    // 1. Lấy danh sách HOẶC Tìm kiếm
    // JS gọi: fetch("/api/students") hoặc fetch("/api/students?name=abc")
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(service.findByName(name));
        }
        return ResponseEntity.ok(service.getAll());
    }

    // 2. Lấy chi tiết 1 sinh viên
    // JS gọi: fetch("/api/students/1") để đổ dữ liệu vào form sửa
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Integer id) {
        Student student = service.getStudentById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    // 3. Thêm mới
    // JS gọi: POST Body JSON {name, email, gender}
    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        return ResponseEntity.ok(service.addStudent(student));
    }

    // 4. Cập nhật
    // JS gọi: PUT /api/students/1 Body JSON
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Integer id, @RequestBody Student studentDetails) {
        Student existingStudent = service.getStudentById(id);

        if (existingStudent == null) {
            return ResponseEntity.notFound().build();
        }

        // Cập nhật dữ liệu mới vào đối tượng cũ
        existingStudent.setName(studentDetails.getName());
        existingStudent.setEmail(studentDetails.getEmail());
        existingStudent.setGender(studentDetails.getGender());

        // Lưu lại xuống DB
        final Student updatedStudent = service.updateStudent(existingStudent);
        return ResponseEntity.ok(updatedStudent);
    }

    // 5. Xóa
    // JS gọi: DELETE /api/students/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        Student existingStudent = service.getStudentById(id);
        if (existingStudent == null) {
            return ResponseEntity.notFound().build();
        }

        service.deleteStudent(id);
        return ResponseEntity.ok().build();
    }
}
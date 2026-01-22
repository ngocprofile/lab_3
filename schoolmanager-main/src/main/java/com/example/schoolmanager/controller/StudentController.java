package com.example.schoolmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin // Giữ lại để đảm bảo không bị chặn nếu Security config chưa chuẩn
public class StudentController {

    @Autowired
    private StudentService service;

    // 1. Lấy danh sách HOẶC Tìm kiếm (Gộp chung để tránh lỗi param)
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents(@RequestParam(required = false) String name) {
        // Nếu có tham số name gửi lên thì tìm kiếm
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(service.findByName(name));
        }
        // Nếu không có thì trả về tất cả
        return ResponseEntity.ok(service.getAll());
    }

    // 2. Lấy chi tiết 1 sinh viên (để hiển thị lên form sửa)
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Integer id) {
        Student student = service.getStudentById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    // 3. Thêm mới (Sửa: Thêm @RequestBody để nhận JSON)
    @PostMapping
    public ResponseEntity<Student> addStudent(@RequestBody Student student) {
        return ResponseEntity.ok(service.addStudent(student));
    }

    // 4. Cập nhật (Sửa: Dùng PUT và @RequestBody)
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Integer id, @RequestBody Student studentDetails) {
        Student existingStudent = service.getStudentById(id);

        if (existingStudent == null) {
            return ResponseEntity.notFound().build();
        }

        // Cập nhật thông tin từ JSON gửi lên
        existingStudent.setName(studentDetails.getName());
        existingStudent.setEmail(studentDetails.getEmail());
        existingStudent.setGender(studentDetails.getGender());

        final Student updatedStudent = service.updateStudent(existingStudent);
        return ResponseEntity.ok(updatedStudent);
    }

    // 5. Xóa (Sửa: Dùng DELETE)
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
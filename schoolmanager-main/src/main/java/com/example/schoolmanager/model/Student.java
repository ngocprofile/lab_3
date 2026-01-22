package com.example.schoolmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Đã chuẩn (Integer)

    private String name;
    private String email;
    private String gender; // Tên biến chuẩn

    public Student() {
    }

    public Student(Integer id, String name, String email, String gender) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
    }

    // --- ID (Chuẩn) ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // --- Name ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // --- Email ---
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // --- GENDER (SỬA LẠI TÊN HÀM Ở ĐÂY) ---
    public String getGender() { // Sửa getGentle -> getGender
        return gender;
    }

    public void setGender(String gender) { // Sửa setGentle -> setGender
        this.gender = gender;
    }
}
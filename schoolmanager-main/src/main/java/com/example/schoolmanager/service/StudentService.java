package com.example.schoolmanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.schoolmanager.model.Student;
import com.example.schoolmanager.respository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    private StudentRepository repository;

    public Student addStudent(Student student) {
        return repository.save(student);
    }

    public void deleteStudent(Integer id) {
        repository.deleteById(id);
    }

    public List<Student> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    public List<Student> getAll() {
        return repository.findAll();
    }

    public Student getStudentById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public Student updateStudent(Student student) {
        System.out.println(">>> UPDATE STUDENT SERVICE CALLED <<<");
        return repository.save(student);
    }
}
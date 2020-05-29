package com.nuti.puccia.repository;

import com.nuti.puccia.model.Student;

import java.util.List;

public interface StudentRepository {
    void deleteStudent(Student student);

    void addStudent(Student student);

    List<Student> findAll();

    Student findById(long id);
}

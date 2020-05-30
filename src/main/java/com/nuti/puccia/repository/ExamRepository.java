package com.nuti.puccia.repository;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;

import java.util.List;

public interface ExamRepository {
    void addExam(Exam exam);

    void deleteExam(Exam exam);

    void addReservation(Exam exam, Student student);

    void deleteReservation(Exam exam, Student student);

    void deleteStudentReservations(Student student);

    List<Exam> findAll();

    Exam findById(long id);
}

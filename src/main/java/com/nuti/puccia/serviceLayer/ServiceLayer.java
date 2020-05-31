package com.nuti.puccia.serviceLayer;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.repository.ExamRepository;
import com.nuti.puccia.repository.StudentRepository;

import java.util.List;

public class ServiceLayer {

    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;

    public ServiceLayer(StudentRepository studentRepository, ExamRepository examRepository) {
        this.studentRepository = studentRepository;
        this.examRepository = examRepository;
    }

    public void addStudent(Student student) {
        studentRepository.addStudent(student);
    }

    public void deleteStudent(Student student) {
        if (studentRepository.findById(student.getId()) == null)
            throw new IllegalArgumentException("Student does not exist");
        studentRepository.deleteStudent(student);
        examRepository.deleteStudentReservations(student);
    }

    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    public void addExam(Exam exam) {
        examRepository.addExam(exam);
    }

    public void deleteExam(Exam exam) {
        if (examRepository.findById(exam.getId()) == null)
            throw new IllegalArgumentException("Exam does not exist");
        examRepository.deleteExam(exam);
    }

    public List<Exam> findAllExams() {
        return examRepository.findAll();
    }

    public void addReservation(Exam exam, Student student) {
        examRepository.addReservation(exam, student);
    }

    public void deleteReservation(Exam exam, Student student) {
        examRepository.deleteReservation(exam, student);
    }
}

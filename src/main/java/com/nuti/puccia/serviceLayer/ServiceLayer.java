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

    public void deleteStudent(Student student) throws IllegalArgumentException {
        if (studentRepository.findById(student.getId()) == null)
            throw new IllegalArgumentException("Student " + student.toString() + " does not exist!");
        studentRepository.deleteStudent(student);
        examRepository.deleteStudentReservations(student);
    }

    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }

    public void addExam(Exam exam) {
        examRepository.addExam(exam);
    }

    public void deleteExam(Exam exam) throws IllegalArgumentException {
        if (examRepository.findById(exam.getId()) == null)
            throw new IllegalArgumentException("Exam " + exam.toString() + " does not exist!");
        examRepository.deleteExam(exam);
    }

    public List<Exam> findAllExams() {
        return examRepository.findAll();
    }

    public void addReservation(Exam exam, Student student) throws IllegalArgumentException {
        examRepository.addReservation(exam, student);
    }

    public void deleteReservation(Exam exam, Student student) throws IllegalArgumentException {
        examRepository.deleteReservation(exam, student);
    }
}

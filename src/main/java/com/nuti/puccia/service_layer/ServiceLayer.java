package com.nuti.puccia.service_layer;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.transaction_manager.TransactionManager;

import java.util.List;

public class ServiceLayer {

    private final TransactionManager transactionManager;

    public ServiceLayer(TransactionManager manger) {
        transactionManager = manger;
    }

    public List<Student> findAllStudents() {
        return transactionManager.executeTransaction(
                (examRepository, studentRepository) -> studentRepository.findAll());
    }

    public List<Exam> findAllExams() {
        return transactionManager.executeTransaction(
                (examRepository, studentRepository) -> examRepository.findAll());
    }

    public void addStudent(Student student) {
        transactionManager.executeTransaction(
                (examRepository, studentRepository) -> {
                    studentRepository.addStudent(student);
                    return null;
                });
    }

    public void addExam(Exam exam) {
        transactionManager.executeTransaction(
                (examRepository, studentRepository) -> {
                    examRepository.addExam(exam);
                    return null;
                });
    }

    public void deleteReservation(Exam exam, Student student) {
        transactionManager.executeTransaction(
                (examRepository, studentRepository) -> {
                    examRepository.deleteReservation(exam, student);
                    return null;
                });
    }

    public void deleteStudent(Student student) {
        transactionManager.executeTransaction(
                (examRepository, studentRepository) -> {
                    examRepository.deleteStudentReservations(student);
                    studentRepository.deleteStudent(student);
                    return null;
                }, "Student " + student.toString() + " does not exist!");

    }


    public void deleteExam(Exam exam) {
        transactionManager.executeTransaction(
                (examRepository, studentRepository) -> {
                    examRepository.deleteExam(exam);
                    return null;
                }, "Exam " + exam.toString() + " does not exist!");

    }


    public void addReservation(Exam exam, Student student) {
        transactionManager.executeTransaction(
                (examRepository, studentRepository) -> {
                    examRepository.addReservation(exam, student);
                    return null;
                }, "Student " + student.toString() + " already present in " + exam.toString() + "!");
    }


}

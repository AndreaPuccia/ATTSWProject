package com.nuti.puccia.controller;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.service_layer.ServiceLayer;
import com.nuti.puccia.view.ExamReservationsView;

public class Controller {

    private final ExamReservationsView view;
    private final ServiceLayer serviceLayer;

    public Controller(ExamReservationsView view, ServiceLayer serviceLayer) {
        this.view = view;
        this.serviceLayer = serviceLayer;
    }

    public void addStudent(Student student) {
        serviceLayer.addStudent(student);
        showAllStudents();
    }

    public void deleteStudent(Student student) {
        try {
            serviceLayer.deleteStudent(student);
            showAllStudents();
            view.updateReservations();
        } catch (IllegalArgumentException e) {
            showAllStudents();
            view.updateReservations();
            view.showError(e.getMessage());
        }
    }

    public void showAllStudents() {
        view.updateStudents(serviceLayer.findAllStudents());
    }

    public void addExam(Exam exam) {
        serviceLayer.addExam(exam);
        showAllExams();
    }

    public void deleteExam(Exam exam) {
        try {
            serviceLayer.deleteExam(exam);
            showAllExams();
        } catch (IllegalArgumentException e) {
            showAllExams();
            view.showError(e.getMessage());
        }
    }

    public void showAllExams() {
        view.updateExams(serviceLayer.findAllExams());
    }

    public void addReservation(Exam exam, Student student) {
        try {
            serviceLayer.addReservation(exam, student);
            view.updateReservations();
        } catch (IllegalArgumentException e) {
            view.updateReservations();
            view.showError(e.getMessage());
        }
    }

    public void deleteReservation(Exam exam, Student student) {
        try {
            serviceLayer.deleteReservation(exam, student);
            view.updateReservations();
        } catch (IllegalArgumentException e) {
            view.updateReservations();
            view.showError(e.getMessage());
        }
    }
}

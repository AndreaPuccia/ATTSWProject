package com.nuti.puccia.ut;

import com.nuti.puccia.controller.Controller;
import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.service_layer.ServiceLayer;
import com.nuti.puccia.view.ExamReservationsView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;


public class ControllerTest {

    @Mock
    private ExamReservationsView view;
    @Mock
    private ServiceLayer serviceLayer;
    @InjectMocks
    private Controller controller;

    private InOrder inOrder;

    private Student student;
    private List<Student> students;
    private Exam exam;
    private List<Exam> exams;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        inOrder = inOrder(serviceLayer, view);

        student = new Student("Andrea", "Puccia");
        students = new ArrayList<>(Collections.singletonList(student));
        exam = new Exam("ATTSW", new ArrayList<>());
        exams = new ArrayList<>(Collections.singletonList(exam));
    }

    @Test
    public void addStudent() {
        when(serviceLayer.findAllStudents()).thenReturn(students);
        controller.addStudent(student);
        inOrder.verify(serviceLayer).addStudent(student);
        inOrder.verify(view).updateStudents(students);
    }


    @Test
    public void deleteStudentWhenItExists() {
        when(serviceLayer.findAllStudents()).thenReturn(students);
        controller.deleteStudent(student);
        inOrder.verify(serviceLayer).deleteStudent(student);
        inOrder.verify(view).updateStudents(students);
        inOrder.verify(view).updateReservations();
    }


    @Test
    public void deleteStudentWhenItDoesNotExist() {
        when(serviceLayer.findAllStudents()).thenReturn(students);
        doThrow(new IllegalArgumentException("Error message")).when(serviceLayer).deleteStudent(student);
        controller.deleteStudent(student);
        inOrder.verify(view).updateStudents(students);
        inOrder.verify(view).updateReservations();
        inOrder.verify(view).showError("Error message");
    }


    @Test
    public void showAllStudents() {
        when(serviceLayer.findAllStudents()).thenReturn(students);
        controller.showAllStudents();
        verify(view).updateStudents(students);
    }

    @Test
    public void addExam() {
        when(serviceLayer.findAllExams()).thenReturn(exams);
        controller.addExam(exam);
        inOrder.verify(serviceLayer).addExam(exam);
        inOrder.verify(view).updateExams(exams);
    }


    @Test
    public void deleteExamWhenItExists() {
        when(serviceLayer.findAllExams()).thenReturn(exams);
        controller.deleteExam(exam);
        inOrder.verify(serviceLayer).deleteExam(exam);
        inOrder.verify(view).updateExams(exams);
    }


    @Test
    public void deleteExamWhenItDoesNotExist() {
        when(serviceLayer.findAllExams()).thenReturn(exams);
        doThrow(new IllegalArgumentException("Error message")).when(serviceLayer).deleteExam(exam);
        controller.deleteExam(exam);
        inOrder.verify(view).updateExams(exams);
        inOrder.verify(view).showError("Error message");
    }


    @Test
    public void showAllExams() {
        when(serviceLayer.findAllExams()).thenReturn(exams);
        controller.showAllExams();
        verify(view).updateExams(exams);
    }

    @Test
    public void addReservationWhenItDoesNotExist() {
        controller.addReservation(exam, student);
        inOrder.verify(serviceLayer).addReservation(exam, student);
        inOrder.verify(view).updateReservations();
    }

    @Test
    public void addReservationWhenItExists() {
        doThrow(new IllegalArgumentException("Error message")).when(serviceLayer).addReservation(exam, student);
        controller.addReservation(exam, student);
        inOrder.verify(view).updateReservations();
        inOrder.verify(view).showError("Error message");
    }

    @Test
    public void deleteReservationWhenItExists() {
        controller.deleteReservation(exam, student);
        inOrder.verify(serviceLayer).deleteReservation(exam, student);
        inOrder.verify(view).updateReservations();
    }

    @Test
    public void deleteReservationWhenItDoesNotExist() {
        doThrow(new IllegalArgumentException("Error message")).when(serviceLayer).deleteReservation(exam, student);
        controller.deleteReservation(exam, student);
        inOrder.verify(view).updateReservations();
        inOrder.verify(view).showError("Error message");
    }

}

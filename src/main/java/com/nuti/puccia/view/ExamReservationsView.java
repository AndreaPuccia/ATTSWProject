package com.nuti.puccia.view;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;

import java.util.List;

public interface ExamReservationsView {

    void updateStudents(List<Student> students);

    void updateExams(List<Exam> exams);

    void showError(String message);
}

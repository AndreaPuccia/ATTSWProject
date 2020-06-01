package com.nuti.puccia.ut;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExamTest {

    private Exam exam;
    private List<Student> students;

    @Before
    public void setup() {
        students = new ArrayList<>();
        exam = new Exam("ATTSW", students);
    }

    @Test
    public void addNewStudentToExam() {
        Student student = new Student("Andrea", "Puccia");
        exam.addStudent(student);
        assertThat(students).containsExactly(student);
    }

    @Test
    public void addExistingStudentToExam() {
        Student student = new Student("Andrea", "Puccia");
        students.add(student);
        assertThatThrownBy(() -> exam.addStudent(student))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Student " + student.toString() + " already present in " + exam.toString() + "!");
    }

    @Test
    public void removeExistingStudentFromExam() {
        Student student1 = new Student("Lorenzo", "Nuti");
        Student student2 = new Student("Andrea", "Puccia");
        students.add(student1);
        students.add(student2);
        exam.removeStudent(student1);
        assertThat(students).containsExactly(student2);
    }

    @Test
    public void removeStudentFromExamWithEmptyExams() {
        Student student = new Student("Andrea", "Puccia");
        assertThatThrownBy(() -> exam.removeStudent(student))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Student " + student.toString() + " not present in " + exam.toString() + "!");
        assertThat(students).isEmpty();
    }

    @Test
    public void removeStudentFromExamWithoutThisExam() {
        Student student1 = new Student("Lorenzo", "Nuti");
        students.add(student1);
        Student student2 = new Student("Andrea", "Puccia");
        assertThatThrownBy(() -> exam.removeStudent(student2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Student " + student2.toString() + " not present in " + exam.toString() + "!");
        assertThat(students).containsExactly(student1);
    }
}

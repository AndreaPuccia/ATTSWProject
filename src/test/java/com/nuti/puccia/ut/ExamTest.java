package com.nuti.puccia.ut;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExamTest {

    private Exam exam;
    private Set<Student> students;

    @Before
    public void setup() {
        students = new LinkedHashSet<>();
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
                .isInstanceOf(Error.class)
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
    public void removeStudentFromExamWithEmptyStudents() {
        Student student = new Student("Andrea", "Puccia");
        exam.removeStudent(student);
        assertThat(students).isEmpty();
    }

    @Test
    public void removeStudentFromExamWithoutThisStudent() {
        Student student1 = new Student("Lorenzo", "Nuti");
        students.add(student1);
        Student student2 = new Student("Andrea", "Puccia");
        exam.removeStudent(student2);
        assertThat(students).containsExactly(student1);
    }
}

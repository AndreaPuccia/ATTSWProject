package com.nuti.puccia.ut;

import com.nuti.puccia.controller.Controller;
import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.view.swing.ExamReservationsSwingView;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JListFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(GUITestRunner.class)
public class ViewTest extends AssertJSwingJUnitTestCase {
    private Student student1;
    private Student student2;
    private Exam exam1;
    private Exam exam2;
    private FrameFixture window;
    private ExamReservationsSwingView view;
    @Mock
    private Controller controller;

    @Override
    protected void onSetUp() {
        MockitoAnnotations.initMocks(this);
        GuiActionRunner.execute(() -> {
            view = new ExamReservationsSwingView();
            view.setController(controller);
            return view;
        });
        window = new FrameFixture(robot(), view);
        window.show();

        student1 = new Student("Andrea", "Puccia");
        student2 = new Student("Lorenzo", "Nuti");
        exam1 = spy(new Exam("ATTSW", new LinkedHashSet<>(Arrays.asList(student1, student2))));
        exam2 = spy(new Exam("Analisi", new LinkedHashSet<>(Collections.singletonList(student1))));
    }


    @Test
    @GUITest
    public void initialState() {
        assertThat(window.button("AddExam").isEnabled()).isFalse();
        assertThat(window.button("AddReservation").isEnabled()).isFalse();
        assertThat(window.button("AddStudent").isEnabled()).isFalse();
        assertThat(window.textBox("ExamNameText").isEnabled()).isTrue();
        assertThat(window.textBox("StudentNameText").isEnabled()).isTrue();
        assertThat(window.textBox("StudentSurnameText").isEnabled()).isTrue();
        assertThat(window.button("DeleteExam").isEnabled()).isFalse();
        assertThat(window.button("DeleteReservation").isEnabled()).isFalse();
        assertThat(window.button("DeleteStudent").isEnabled()).isFalse();
        assertThat(window.label("ReservationLabel").text()).isEqualTo("Select a student to add");
        assertThat(window.label("ErrorLabel").text()).isEqualTo("");
    }

    @Test
    @GUITest
    public void enablingAddExamButtonWhenNameTextIsNotEmpty() {
        window.textBox("ExamNameText").enterText("ATTSW");
        assertThat(window.button("AddExam").isEnabled()).isTrue();
        window.textBox("ExamNameText").deleteText();
        assertThat(window.button("AddExam").isEnabled()).isFalse();
    }

    @Test
    @GUITest
    public void disablingAddExamButtonWhenNameTextIsBlank() {
        window.textBox("ExamNameText").enterText(" ");
        assertThat(window.button("AddExam").isEnabled()).isFalse();
    }

    @Test
    @GUITest
    public void enablingAddStudentButtonWhenNameAndSurnameTextAreNotEmpty() {
        window.textBox("StudentNameText").enterText("Andrea");
        window.textBox("StudentSurnameText").enterText("Puccia");
        assertThat(window.button("AddStudent").isEnabled()).isTrue();

        window.textBox("StudentNameText").deleteText();
        assertThat(window.button("AddStudent").isEnabled()).isFalse();

        window.textBox("StudentNameText").enterText("Andrea");
        window.textBox("StudentSurnameText").deleteText();
        assertThat(window.button("AddStudent").isEnabled()).isFalse();
    }

    @Test
    @GUITest
    public void disablingAddStudentButtonWhenNameOrSurnameTextAreBlank() {
        JTextComponentFixture studentName = window.textBox("StudentNameText");
        JTextComponentFixture studentSurname = window.textBox("StudentSurnameText");

        studentName.enterText(" ");
        studentSurname.enterText("Puccia");
        assertThat(window.button("AddStudent").isEnabled()).isFalse();

        studentName.setText("");
        studentSurname.setText("");

        studentName.enterText("Andrea");
        studentSurname.enterText(" ");
        assertThat(window.button("AddStudent").isEnabled()).isFalse();
    }

    @Test
    @GUITest
    public void enablingAddReservationButtonWhenAStudentAndAnExamAreSelected() {
        GuiActionRunner.execute(() -> {
            view.getExamModel().addElement(exam1);
            view.getStudentModel().addElement(student1);
        });
        window.list("ExamList").selectItem(0);
        window.list("StudentList").selectItem(0);
        assertThat(window.button("AddReservation").isEnabled()).isTrue();
        assertThat(window.label("ReservationLabel").text()).isEqualTo(student1.toString());
    }

    @Test
    @GUITest
    public void disablingAddReservationButtonWhenExamOrStudentAreNotSelected() {
        GuiActionRunner.execute(() -> {
            view.getExamModel().addElement(exam1);
            view.getStudentModel().addElement(student1);
        });
        JListFixture examList = window.list("ExamList");
        JListFixture studentList = window.list("StudentList");

        examList.selectItem(0);
        studentList.selectItem(0);
        studentList.clearSelection();

        assertThat(window.button("AddReservation").isEnabled()).isFalse();
        assertThat(window.label("ReservationLabel").text()).isEqualTo("Select a student to add");

        studentList.selectItem(0);
        examList.clearSelection();

        assertThat(window.button("AddReservation").isEnabled()).isFalse();
        assertThat(window.label("ReservationLabel").text()).isEqualTo("Select a student to add");
    }

    @Test
    @GUITest
    public void enablingDeleteExamButtonOnlyWhenAnExamIsSelected() {
        GuiActionRunner.execute(() -> view.getExamModel().addElement(exam1));
        window.list("ExamList").selectItem(0);
        assertThat(window.button("DeleteExam").isEnabled()).isTrue();
        window.list("ExamList").clearSelection();
        assertThat(window.button("DeleteExam").isEnabled()).isFalse();
    }

    @Test
    @GUITest
    public void enablingDeleteStudentButtonOnlyWhenAStudentIsSelected() {
        GuiActionRunner.execute(() -> view.getStudentModel().addElement(student1));
        window.list("StudentList").selectItem(0);
        assertThat(window.button("DeleteStudent").isEnabled()).isTrue();
        window.list("StudentList").clearSelection();
        assertThat(window.button("DeleteStudent").isEnabled()).isFalse();
    }

    @Test
    @GUITest
    public void enablingDeleteReservationButtonOnlyWhenAReservationIsSelected() {
        GuiActionRunner.execute(() -> view.getReservationModel().addElement(student1));
        window.list("ReservationList").selectItem(0);
        assertThat(window.button("DeleteReservation").isEnabled()).isTrue();
        window.list("ReservationList").clearSelection();
        assertThat(window.button("DeleteReservation").isEnabled()).isFalse();
    }

    @Test
    @GUITest
    public void showReservationsForAnExamOnlyWhenItIsSelected() {
        GuiActionRunner.execute(() -> view.getExamModel().addElement(exam1));
        window.list("ExamList").selectItem(0);
        assertThat(window.list("ReservationList").contents())
                .containsExactly(student1.toString(), student2.toString());
        window.list("ExamList").clearSelection();
        assertThat(window.list("ReservationList").contents()).isEmpty();
    }

    @Test
    @GUITest
    public void showReservationsForAnExamWhenModelContainsAlreadyAReservation() {
        GuiActionRunner.execute(() -> {
            view.getExamModel().addElement(exam1);
            view.getReservationModel().addElement(student1);
        });
        window.list("ExamList").selectItem(0);
        assertThat(window.list("ReservationList").contents())
                .containsExactly(student1.toString(), student2.toString());
    }

    @Test
    @GUITest
    public void showExamsOnUpdateExams() {
        GuiActionRunner.execute(() -> {
            view.getErrorLabel().setText("Error message");
            view.updateExams(new ArrayList<>(Arrays.asList(exam1, exam2)));
        });
        assertThat(window.list("ExamList").contents()).containsExactly(exam1.toString(), exam2.toString());
        assertThat(window.label("ErrorLabel").text()).isEqualTo("");
    }

    @Test
    @GUITest
    public void showExamsOnUpdateExamsClearExams() {
        GuiActionRunner.execute(() -> {
            view.getErrorLabel().setText("Error message");
            view.getExamModel().addElement(exam1);
            view.getExamModel().addElement(exam2);
            view.updateExams(new ArrayList<>(Arrays.asList(exam2, exam1)));
        });
        assertThat(window.list("ExamList").contents()).containsExactly(exam2.toString(), exam1.toString());
        assertThat(window.list("ExamList").selection()).isEmpty();
        assertThat(window.label("ErrorLabel").text()).isEqualTo("");
    }


    @Test
    @GUITest
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void showExamsOnUpdateExamsWhenExamSelected() {
        GuiActionRunner.execute(() -> {
            view.getExamModel().addElement(exam1);
            view.getExamModel().addElement(exam2);
            doReturn(1L).when(exam1).getId();
            doReturn(2L).when(exam2).getId();
        });
        window.list("ExamList").selectItem(0);
        GuiActionRunner.execute(() -> view.updateExams(new ArrayList<>(Arrays.asList(exam2, exam1))));
        assertThat(window.list("ExamList").contents()).containsExactly(exam2.toString(), exam1.toString());
        assertThat(window.list("ExamList").selection()).containsExactly(exam1.toString());
    }

    @Test
    @GUITest
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void showExamsOnUpdateExamsWhenExamSelectedDoesNotExist() {
        GuiActionRunner.execute(() -> {
            view.getExamModel().addElement(exam1);
            view.getExamModel().addElement(exam2);
            doReturn(1L).when(exam1).getId();
            doReturn(2L).when(exam2).getId();
        });
        window.list("ExamList").selectItem(0);
        GuiActionRunner.execute(() -> view.updateExams(new ArrayList<>(Collections.singletonList(exam2))));
        assertThat(window.list("ExamList").contents()).containsExactly(exam2.toString());
        assertThat(window.list("ExamList").selection()).isEmpty();
    }

    @Test
    @GUITest
    public void showStudentsOnUpdateStudents() {
        GuiActionRunner.execute(() -> {
            view.getErrorLabel().setText("Error message");
            view.updateStudents(new ArrayList<>(Arrays.asList(student1, student2)));
        });
        assertThat(window.list("StudentList").contents()).containsExactly(student1.toString(), student2.toString());
        assertThat(window.label("ErrorLabel").text()).isEqualTo("");
    }

    @Test
    @GUITest
    public void showStudentsOnUpdateStudentsWhenClearStudents() {
        GuiActionRunner.execute(() -> {
            view.getErrorLabel().setText("Error message");
            view.getStudentModel().addElement(student1);
            view.getStudentModel().addElement(student2);
            view.updateStudents(new ArrayList<>(Arrays.asList(student2, student1)));
        });
        assertThat(window.list("StudentList").contents()).containsExactly(student2.toString(), student1.toString());
        assertThat(window.label("ErrorLabel").text()).isEqualTo("");
    }


    @Test
    @GUITest
    public void showErrorInErrorLabel() {
        GuiActionRunner.execute(() -> view.showError("Error message"));
        assertThat(window.label("ErrorLabel").text()).isEqualTo("Error message");
    }

    @Test
    @GUITest
    public void addExamClickDelegatedToController() {
        window.textBox("ExamNameText").enterText("ATTSW");
        window.button("AddExam").click();
        verify(controller).addExam(new Exam("ATTSW", new LinkedHashSet<>()));
    }

    @Test
    @GUITest
    public void addStudentClickDelegatedToController() {
        window.textBox("StudentNameText").enterText("Andrea");
        window.textBox("StudentSurnameText").enterText("Puccia");
        window.button("AddStudent").click();
        verify(controller).addStudent(new Student("Andrea", "Puccia"));
    }

    @Test
    @GUITest
    public void addReservationClickDelegatedToController() {
        GuiActionRunner.execute(() -> {
            view.getExamModel().addElement(exam1);
            view.getStudentModel().addElement(student1);
        });
        window.list("ExamList").selectItem(0);
        window.list("StudentList").selectItem(0);
        window.button("AddReservation").click();
        verify(controller).addReservation(exam1, student1);
    }

    @Test
    @GUITest
    public void deleteExamClickDelegatedToController() {
        GuiActionRunner.execute(() -> view.getExamModel().addElement(exam1));
        window.list("ExamList").selectItem(0);
        window.button("DeleteExam").click();
        verify(controller).deleteExam(exam1);
    }

    @Test
    @GUITest
    public void deleteStudentClickDelegatedToController() {
        GuiActionRunner.execute(() -> view.getStudentModel().addElement(student1));
        window.list("StudentList").selectItem(0);
        window.button("DeleteStudent").click();
        verify(controller).deleteStudent(student1);
    }

    @Test
    @GUITest
    public void deleteReservationClickDelegatedToController() {
        GuiActionRunner.execute(() -> view.getExamModel().addElement(exam1));
        window.list("ExamList").selectItem(0);
        window.list("ReservationList").selectItem(0);
        window.button("DeleteReservation").click();
        verify(controller).deleteReservation(exam1, student1);
    }


}

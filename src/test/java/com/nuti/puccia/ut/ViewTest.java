package com.nuti.puccia.ut;

import com.nuti.puccia.controller.Controller;
import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.view.swing.ExamReservationsSwingView;
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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(GUITestRunner.class)
public class ViewTest extends AssertJSwingJUnitTestCase {
    private static final Student student1 = new Student("Andrea", "Puccia");
    private static final Student student2 = new Student("Lorenzo", "Nuti");
    private static final Exam exam1 = new Exam("ATTSW", new ArrayList<>(Arrays.asList(student1, student2)));
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
    }


    @Test
    public void initialState() {
        assertThat(window.button("AddExam").isEnabled()).isFalse();
        assertThat(window.button("AddReservation").isEnabled()).isFalse();
        assertThat(window.button("AddStudent").isEnabled()).isFalse();
        assertThat(window.textBox("ExamNameText").isEnabled()).isTrue();
        assertThat(window.textBox("StudentNameText").isEnabled()).isTrue();
        assertThat(window.textBox("StudentSurnameText").isEnabled()).isTrue();
        assertThat(window.button("RemoveExam").isEnabled()).isFalse();
        assertThat(window.button("RemoveReservation").isEnabled()).isFalse();
        assertThat(window.button("RemoveStudent").isEnabled()).isFalse();
        assertThat(window.label("ReservationLabel").text()).isEqualTo("Select a student to add a reservation");
        assertThat(window.label("ErrorLabel").text()).isEqualTo("");
    }

    @Test
    public void enablingAddExamButtonWhenNameTextIsNotEmpty() {
        window.textBox("ExamNameText").enterText("ATTSW");
        assertThat(window.button("AddExam").isEnabled()).isTrue();
        window.textBox("ExamNameText").deleteText();
        assertThat(window.button("AddExam").isEnabled()).isFalse();
    }

    @Test
    public void disablingAddExamButtonWhenNameTextIsBlank() {
        window.textBox("ExamNameText").enterText(" ");
        assertThat(window.button("AddExam").isEnabled()).isFalse();
    }

    @Test
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
    public void enablingAddReservationButtonWhenAStudentAndAnExamAreSelected() {
        GuiActionRunner.execute(() -> view.getExamModel().addElement(exam1));
        GuiActionRunner.execute(() -> view.getStudentModel().addElement(student1));
        window.list("ExamList").selectItem(0);
        window.list("StudentList").selectItem(0);
        assertThat(window.button("AddReservation").isEnabled()).isTrue();
        assertThat(window.label("ReservationLabel").text()).isEqualTo(student1.toString());
    }

    @Test
    public void disablingAddReservationButtonWhenExamOrStudentAreNotSelected() {
        GuiActionRunner.execute(() -> view.getExamModel().addElement(exam1));
        GuiActionRunner.execute(() -> view.getStudentModel().addElement(student1));
        JListFixture examList = window.list("ExamList");
        JListFixture studentList = window.list("StudentList");

        examList.selectItem(0);
        assertThat(window.button("AddReservation").isEnabled()).isFalse();
        assertThat(window.label("ReservationLabel").text()).isEqualTo("Select a student to add a reservation");

        examList.clearSelection();

        studentList.selectItem(0);
        assertThat(window.button("AddReservation").isEnabled()).isFalse();
        assertThat(window.label("ReservationLabel").text()).isEqualTo("Select a student to add a reservation");
    }

    @Test
    public void enablingDeleteExamButtonOnlyWhenAnExamIsSelected() {
        GuiActionRunner.execute(() -> view.getExamModel().addElement(exam1));
        window.list("ExamList").selectItem(0);
        assertThat(window.button("RemoveExam").isEnabled()).isTrue();
        window.list("ExamList").clearSelection();
        assertThat(window.button("RemoveExam").isEnabled()).isFalse();
    }

    @Test
    public void enablingDeleteStudentButtonOnlyWhenAStudentIsSelected() {
        GuiActionRunner.execute(() -> view.getStudentModel().addElement(student1));
        window.list("StudentList").selectItem(0);
        assertThat(window.button("RemoveStudent").isEnabled()).isTrue();
        window.list("StudentList").clearSelection();
        assertThat(window.button("RemoveStudent").isEnabled()).isFalse();
    }

    @Test
    public void enablingDeleteReservationButtonOnlyWhenAReservationIsSelected() {
        GuiActionRunner.execute(() -> view.getReservationModel().addElement(student1));
        window.list("ReservationList").selectItem(0);
        assertThat(window.button("RemoveReservation").isEnabled()).isTrue();
        window.list("ReservationList").clearSelection();
        assertThat(window.button("RemoveReservation").isEnabled()).isFalse();
    }

    @Test
    public void showReservationsForAnExamOnlyWhenItIsSelected() {
        GuiActionRunner.execute(() -> view.getExamModel().addElement(exam1));
        window.list("ExamList").selectItem(0);
        assertThat(window.list("ReservationList").contents())
                .containsExactly(student1.toString(), student2.toString());
        window.list("ExamList").clearSelection();
        assertThat(window.list("ReservationList").contents()).isEmpty();
    }

    @Test
    public void showReservationsForAnExamWhenModelContainsAlreadyAReservation() {
        GuiActionRunner.execute(() -> view.getExamModel().addElement(exam1));
        GuiActionRunner.execute(() -> view.getReservationModel().addElement(student1));
        window.list("ExamList").selectItem(0);
        assertThat(window.list("ReservationList").contents())
                .containsExactly(student1.toString(), student2.toString());
    }

}

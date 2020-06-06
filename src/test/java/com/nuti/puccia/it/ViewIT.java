package com.nuti.puccia.it;

import com.nuti.puccia.controller.Controller;
import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.repository.StudentRepository;
import com.nuti.puccia.repository.mysql.ExamRepositoryMysql;
import com.nuti.puccia.repository.mysql.StudentRepositoryMysql;
import com.nuti.puccia.service_layer.ServiceLayer;
import com.nuti.puccia.view.swing.ExamReservationsSwingView;
import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(GUITestRunner.class)
public class ViewIT extends AssertJSwingJUnitTestCase {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    private ExamRepositoryMysql examRepository;
    private StudentRepository studentRepository;
    private ServiceLayer serviceLayer;
    private ExamReservationsSwingView view;
    private Controller controller;

    private final Student student1 = new Student("Andrea", "Puccia");
    private final Student student2 = new Student("Lorenzo", "Nuti");
    private final Exam exam1 = new Exam("ATTSW", new ArrayList<>(Collections.singletonList(student1)));
    private final Exam exam2 = new Exam("Analisi", new ArrayList<>(Collections.singletonList(student2)));

    private FrameFixture window;

    @BeforeClass
    public static void setUpClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory("TESTS");
    }

    @Override
    protected void onSetUp() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM Exam e").executeUpdate();
        entityManager.createQuery("DELETE FROM Student s").executeUpdate();
        entityManager.getTransaction().commit();

        GuiActionRunner.execute(() -> {
            examRepository = new ExamRepositoryMysql(entityManager);
            studentRepository = new StudentRepositoryMysql(entityManager);
            serviceLayer = new ServiceLayer(studentRepository, examRepository);
            view = new ExamReservationsSwingView();
            controller = new Controller(view, serviceLayer);
            view.setController(controller);
            return view;
        });
        window = new FrameFixture(robot(), view);
        window.show();
    }

    @Override
    public void onTearDown() {
        entityManager.close();
    }

    @AfterClass
    public static void tearDownClass() {
        entityManagerFactory.close();
    }

    @Test
    @GUITest
    public void deleteExamWhenItDoesNotExist() {
        studentRepository.addStudent(student1);
        examRepository.addExam(exam1);
        GuiActionRunner.execute(() -> controller.showAllExams());
        GuiActionRunner.execute(() -> view.getExamModel().addElement(exam2));
        window.list("ExamList").selectItem(1);
        window.button("DeleteExam").click();
        assertThat(window.list("ExamList").contents()).containsExactly(exam1.toString());
        assertThat(window.label("ErrorLabel").text()).isEqualTo("Exam " + exam2.toString() + " does not exist!");
    }

    @Test
    @GUITest
    public void deleteStudentWhenHeDoesNotExist() {
        studentRepository.addStudent(student1);
        GuiActionRunner.execute(() -> controller.showAllStudents());
        GuiActionRunner.execute(() -> view.getStudentModel().addElement(student2));
        window.list("StudentList").selectItem(1);
        window.button("DeleteStudent").click();
        assertThat(window.list("StudentList").contents()).containsExactly(student1.toString());
        assertThat(window.label("ErrorLabel").text()).isEqualTo("Student " + student2.toString() + " does not exist!");
    }

    @Test
    @GUITest
    public void deleteReservationWhenItDoesNotExist() {
        studentRepository.addStudent(student1);
        examRepository.addExam(exam1);
        GuiActionRunner.execute(() -> controller.showAllExams());
        window.list("ExamList").selectItem(0);
        GuiActionRunner.execute(() -> view.getReservationModel().addElement(student2));
        window.list("ReservationList").selectItem(1);
        window.button("DeleteReservation").click();
        assertThat(window.list("ReservationList").contents()).containsExactly(student1.toString());
        assertThat(window.label("ErrorLabel").text()).isEqualTo("Student " + student2.toString() + " not present in " + exam1.toString() + "!");
    }

}

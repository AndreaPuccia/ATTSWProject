package com.nuti.puccia.it;

import com.nuti.puccia.controller.Controller;
import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.repository.StudentRepository;
import com.nuti.puccia.repository.mysql.ExamRepositoryMysql;
import com.nuti.puccia.repository.mysql.StudentRepositoryMysql;
import com.nuti.puccia.service_layer.ServiceLayer;
import com.nuti.puccia.transaction_manager.TransactionManager;
import com.nuti.puccia.transaction_manager.mysql.TransactionManagerMysql;
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
import java.util.LinkedHashSet;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(GUITestRunner.class)
public class ViewIT extends AssertJSwingJUnitTestCase {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    private ExamRepositoryMysql examRepository;
    private StudentRepository studentRepository;
    private ExamReservationsSwingView view;
    private Controller controller;

    private Student student1;
    private Student student2;
    private Exam exam1;
    private Exam exam2;

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
            TransactionManager transactionManager = new TransactionManagerMysql(entityManager);
            ServiceLayer serviceLayer = new ServiceLayer(transactionManager);
            view = new ExamReservationsSwingView();
            controller = new Controller(view, serviceLayer);
            view.setController(controller);
            return view;
        });
        window = new FrameFixture(robot(), view);
        window.show();

        student1 = new Student("Andrea", "Puccia");
        student2 = new Student("Lorenzo", "Nuti");
        exam1 = new Exam("ATTSW", new LinkedHashSet<>());
        exam2 = new Exam("Analisi", new LinkedHashSet<>());
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
    public void deleteExamWhenItDoesNotExistOnDb() {
        entityManager.getTransaction().begin();
        examRepository.addExam(exam1);
        examRepository.addExam(exam2);
        entityManager.getTransaction().commit();
        GuiActionRunner.execute(() -> controller.showAllExams());

        window.list("ExamList").selectItem(0);

        deleteExamFromDataBase(exam2);

        window.button("DeleteExam").click();
        assertThat(window.list("ExamList").contents()).containsExactly(exam1.toString());
        assertThat(window.label("ErrorLabel").text()).isEqualTo("Exam " + exam2.toString() + " does not exist!");
    }


    @Test
    @GUITest
    public void deleteStudentWhenHeDoesNotExistOnDb() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addReservation(exam1, student2);
        entityManager.getTransaction().commit();
        GuiActionRunner.execute(() -> {
            controller.showAllStudents();
            controller.showAllExams();
        });

        window.list("StudentList").selectItem(0);
        window.list("ExamList").selectItem(0);

        deleteStudentFromDataBase(student2);

        window.button("DeleteStudent").click();
        assertThat(window.list("StudentList").contents()).containsExactly(student1.toString());
        assertThat(window.list("ExamList").selection()).containsExactly(exam1.toString());
        assertThat(window.list("ReservationList").contents()).isEmpty();
        assertThat(window.label("ErrorLabel").text()).isEqualTo("Student " + student2.toString() + " does not exist!");
    }

    @Test
    @GUITest
    public void addReservationWhenItAlreadyExistOnDb() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        entityManager.getTransaction().commit();
        GuiActionRunner.execute(() -> {
            controller.showAllStudents();
            controller.showAllExams();
        });
        window.list("ExamList").selectItem(0);
        window.list("StudentList").selectItem(0);

        addReservationToDataBase(exam1, student2);

        window.button("AddReservation").click();
        assertThat(window.list("ExamList").selection()).containsExactly(exam1.toString());
        assertThat(window.list("ReservationList").contents()).containsExactly(student2.toString());
        assertThat(window.label("ErrorLabel").text()).isEqualTo("Student " + student2.toString() + " already present in " + exam1.toString() + "!");
    }

    @Test
    @GUITest
    public void deleteReservationWhenItDoesNotExistOnDb() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addReservation(exam1, student2);
        examRepository.addReservation(exam1, student1);
        entityManager.getTransaction().commit();
        GuiActionRunner.execute(() -> controller.showAllExams());

        window.list("ExamList").selectItem(0);
        window.list("ReservationList").selectItem(0);

        deleteReservationToDataBase(exam1, student2);

        window.button("DeleteReservation").click();
        assertThat(window.list("ExamList").selection()).containsExactly(exam1.toString());
        assertThat(window.list("ReservationList").contents()).containsExactly(student1.toString());
        assertThat(window.label("ErrorLabel").text()).isEqualTo("");
    }

    private void deleteExamFromDataBase(Exam exam) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.find(Exam.class, exam.getId()));
        em.getTransaction().commit();
        em.close();
    }

    private void deleteStudentFromDataBase(Student student) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.find(Student.class, student.getId()));
        em.getTransaction().commit();
        em.close();
    }

    private void addReservationToDataBase(Exam exam, Student student) {
        EntityManager em = entityManagerFactory.createEntityManager();
        Exam e = em.find(Exam.class, exam.getId());
        em.getTransaction().begin();
        e.addStudent(em.find(Student.class, student.getId()));
        em.getTransaction().commit();
        em.close();
    }

    private void deleteReservationToDataBase(Exam exam, Student student) {
        EntityManager em = entityManagerFactory.createEntityManager();
        Exam e = em.find(Exam.class, exam.getId());
        em.getTransaction().begin();
        e.removeStudent(em.find(Student.class, student.getId()));
        em.getTransaction().commit();
        em.close();
    }

}

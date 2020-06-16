package com.nuti.puccia.it;

import com.nuti.puccia.controller.Controller;
import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.repository.StudentRepository;
import com.nuti.puccia.repository.mysql.ExamRepositoryMysql;
import com.nuti.puccia.repository.mysql.StudentRepositoryMysql;
import com.nuti.puccia.service_layer.ServiceLayer;
import com.nuti.puccia.view.ExamReservationsView;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ControllerIT {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @Mock
    private ExamReservationsView view;

    private ExamRepositoryMysql examRepository;
    private StudentRepository studentRepository;
    private Controller controller;

    private Student student1;
    private Student student2;
    private Exam exam1;
    private Exam exam2;


    @BeforeClass
    public static void setUpClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory("TESTS");
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM Exam e").executeUpdate();
        entityManager.createQuery("DELETE FROM Student s").executeUpdate();
        entityManager.getTransaction().commit();

        examRepository = new ExamRepositoryMysql(entityManager);
        studentRepository = new StudentRepositoryMysql(entityManager);
        ServiceLayer serviceLayer = new ServiceLayer(studentRepository, examRepository);
        controller = new Controller(view, serviceLayer);

        student1 = new Student("Andrea", "Puccia");
        student2 = new Student("Lorenzo", "Nuti");
        exam1 = new Exam("ATTSW", new ArrayList<>(Collections.singletonList(student1)));
        exam2 = new Exam("Analisi", new ArrayList<>(Collections.singletonList(student2)));
    }

    @After
    public void tearDown() {
        entityManager.close();
    }

    @AfterClass
    public static void tearDownClass() {
        entityManagerFactory.close();
    }

    @Test
    public void updateStudentsOnAdd() {
        studentRepository.addStudent(student1);
        controller.addStudent(student2);
        verify(view).updateStudents(Arrays.asList(student2, student1));
    }

    @Test
    public void updateStudentsOnDelete() {
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        controller.deleteStudent(student1);
        verify(view).updateStudents(Collections.singletonList(student2));
        verify(view, never()).showError(Mockito.anyString());
    }

    @Test
    public void updateExamsOnAdd() {
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        controller.addExam(exam2);
        verify(view).updateExams(Arrays.asList(exam2, exam1));
    }

    @Test
    public void updateExamsOnDelete() {
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addExam(exam2);
        controller.deleteExam(exam1);
        verify(view).updateExams(Collections.singletonList(exam2));
        verify(view, never()).showError(Mockito.anyString());
    }

    @Test
    public void updateReservationsOnAdd() {
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        controller.addReservation(exam1, student2);
        verify(view).updateReservations();
        assertThat(exam1.getStudents()).containsExactly(student2, student1);
        verify(view, never()).showError(Mockito.anyString());
    }

    @Test
    public void updateReservationsOnDelete() {
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addReservation(exam1, student2);
        controller.deleteReservation(exam1, student1);
        assertThat(exam1.getStudents()).containsExactly(student2);
        verify(view, never()).showError(Mockito.anyString());
    }

    @Test
    public void updateReservationsOnDeleteStudent() {
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addReservation(exam1, student2);
        controller.deleteStudent(student1);
        assertThat(exam1.getStudents()).containsExactly(student2);
        verify(view, never()).showError(Mockito.anyString());
    }

}

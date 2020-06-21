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
import com.nuti.puccia.view.ExamReservationsView;
import org.junit.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ControllerIT {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @Captor
    ArgumentCaptor<List<Student>> argumentCaptor;

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
        TransactionManager transactionManager = new TransactionManagerMysql(entityManager);
        ServiceLayer serviceLayer = new ServiceLayer(transactionManager);
        controller = new Controller(view, serviceLayer);

        student1 = new Student("Andrea", "Puccia");
        student2 = new Student("Lorenzo", "Nuti");
        exam1 = new Exam("ATTSW", new LinkedHashSet<>());
        exam2 = new Exam("Analisi", new LinkedHashSet<>());
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
    public void updateStudentsOnAddStudent() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        entityManager.getTransaction().commit();

        controller.addStudent(student2);
        verify(view).updateStudents(Arrays.asList(student2, student1));
    }

    @Test
    public void updateStudentsAndExamsOnDeleteStudent() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        entityManager.getTransaction().commit();

        controller.deleteStudent(student1);
        verify(view).updateStudents(Collections.singletonList(student2));
        verify(view).updateExams(Collections.singletonList(exam1));
        verify(view, never()).showError(anyString());
    }

    @Test
    public void updateExamsOnAddExam() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        entityManager.getTransaction().commit();

        controller.addExam(exam2);
        verify(view).updateExams(Arrays.asList(exam2, exam1));
    }

    @Test
    public void updateExamsOnDeleteExam() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addExam(exam2);
        entityManager.getTransaction().commit();

        controller.deleteExam(exam1);
        verify(view).updateExams(Collections.singletonList(exam2));
        verify(view, never()).showError(anyString());
    }

    @Test
    public void updateReservationsOnAddReservation() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addReservation(exam1, student1);
        entityManager.getTransaction().commit();

        controller.addReservation(exam1, student2);
        verify(view).updateExams(Collections.singletonList(exam1));
        assertThat(exam1.getStudents()).containsExactly(student2, student1);
        verify(view, never()).showError(anyString());
    }

    @Test
    public void updateReservationsOnDelete() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addReservation(exam1, student1);
        examRepository.addReservation(exam1, student2);
        entityManager.getTransaction().commit();

        controller.deleteReservation(exam1, student1);
        verify(view).updateExams(Collections.singletonList(exam1));
        assertThat(exam1.getStudents()).containsExactly(student2);
        verify(view, never()).showError(anyString());
    }

    @Test
    public void updateReservationsOnDeleteStudent() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addReservation(exam1, student1);
        examRepository.addReservation(exam1, student2);
        entityManager.getTransaction().commit();

        controller.deleteStudent(student1);
        verify(view).updateExams(Collections.singletonList(exam1));
        assertThat(exam1.getStudents()).containsExactly(student2);
        verify(view, never()).showError(anyString());
    }

    @Test
    public void studentsAttachedAfterAddReservationError() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addReservation(exam1, student1);
        entityManager.getTransaction().commit();

        doNothing().when(view).updateStudents(argumentCaptor.capture());
        controller.showAllStudents();
        controller.addReservation(exam1, student1);
        assertThat(argumentCaptor.getValue()).allMatch(student -> entityManager.contains(student),
                "Student is attached");
        verify(view).showError(anyString());
    }

    @Test
    public void studentsAttachedAfterDeleteExamError() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        entityManager.getTransaction().commit();
        deleteExamFromDataBase(exam1);

        doNothing().when(view).updateStudents(argumentCaptor.capture());
        controller.showAllStudents();
        controller.deleteExam(exam1);
        assertThat(argumentCaptor.getValue()).allMatch(student -> entityManager.contains(student),
                "Student is attached");
        verify(view).showError(anyString());
    }

    private void deleteExamFromDataBase(Exam exam) {
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.remove(em.find(Exam.class, exam.getId()));
        em.getTransaction().commit();
        em.close();
    }

}

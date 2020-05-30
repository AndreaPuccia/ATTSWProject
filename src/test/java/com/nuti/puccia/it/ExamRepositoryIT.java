package com.nuti.puccia.it;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.repository.mysql.ExamRepositoryMysql;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ExamRepositoryIT {
    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    private ExamRepositoryMysql examRepository;

    @BeforeClass
    public static void setUpClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory("TESTS");
    }

    @Before
    public void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM Exam e").executeUpdate();
        entityManager.createQuery("DELETE FROM Student s").executeUpdate();
        entityManager.getTransaction().commit();

        examRepository = new ExamRepositoryMysql(entityManager);
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
    public void findAllWhenDataBaseIsEmpty() {
        assertThat(examRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllWhenDataBaseIsNotEmpty() {
        Exam exam = new Exam("ATTSW", new ArrayList<>());
        addTestExamToDataBase(exam);
        assertThat(examRepository.findAll()).containsExactly(exam);
    }

    @Test
    public void findAllInOrderWhenDataBaseIsNotEmpty() {
        Exam exam1 = new Exam("ATTSW", new ArrayList<>());
        addTestExamToDataBase(exam1);
        Exam exam2 = new Exam("Analisi", new ArrayList<>());
        addTestExamToDataBase(exam2);
        assertThat(examRepository.findAll()).containsExactly(exam2, exam1);
    }

    @Test
    public void addNewExamToDatabase() {
        Exam exam1 = new Exam("ATTSW", new ArrayList<>());
        addTestExamToDataBase(exam1);
        Exam exam2 = new Exam("Analisi", new ArrayList<>());
        examRepository.addExam(exam2);
        assertThat(getExamsFromDataBase()).contains(exam1, exam2);
        assertThat(entityManager.getTransaction().isActive()).isFalse();
    }

    @Test
    public void deleteExamFromDataBase() {
        Exam exam = new Exam("ATTSW", new ArrayList<>());
        addTestExamToDataBase(exam);
        examRepository.deleteExam(exam);
        assertThat(getExamsFromDataBase()).isEmpty();
        assertThat(entityManager.getTransaction().isActive()).isFalse();
    }

    @Test
    public void findByIdAnExamWhenItDoesNotExist() {
        Exam exam = new Exam("ATTSW", new ArrayList<>());
        addTestExamToDataBase(exam);
        assertThat(examRepository.findById(0)).isNull();
    }

    @Test
    public void findByIdAnExamWhenItExists() {
        Exam exam1 = new Exam("ATTSW", new ArrayList<>());
        addTestExamToDataBase(exam1);
        Exam exam2 = new Exam("Analisi", new ArrayList<>());
        addTestExamToDataBase(exam2);
        assertThat(examRepository.findById(exam1.getId())).isEqualTo(exam1);
    }

    @Test
    public void addReservationToDataBase() {
        Exam exam = new Exam("ATTSW", new ArrayList<>());
        addTestExamToDataBase(exam);
        Student student = new Student("Andrea", "Puccia");
        addTestStudentToDataBase(student);
        examRepository.addReservation(exam, student);
        entityManager.refresh(exam);
        assertThat(exam.getStudents()).containsExactly(student);
    }

    @Test
    public void addExistingReservationToDataBase() {
        Student student = new Student("Andrea", "Puccia");
        addTestStudentToDataBase(student);
        Exam exam = new Exam("ATTSW", new ArrayList<>(Collections.singletonList(student)));
        addTestExamToDataBase(exam);
        assertThatThrownBy(() -> examRepository.addReservation(exam, student))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(entityManager.getTransaction().isActive()).isFalse();
        entityManager.refresh(exam);
        assertThat(exam.getStudents()).containsExactly(student);
    }

    @Test
    public void deleteReservationFromDataBase() {
        Student student = new Student("Andrea", "Puccia");
        addTestStudentToDataBase(student);
        Exam exam = new Exam("ATTSW", new ArrayList<>(Collections.singletonList(student)));
        addTestExamToDataBase(exam);
        examRepository.deleteReservation(exam, student);
        entityManager.refresh(exam);
        assertThat(exam.getStudents()).isEmpty();
    }

    @Test
    public void deleteReservationFromDataBaseWhenStudentNotPresent() {
        Student student1 = new Student("Andrea", "Puccia");
        addTestStudentToDataBase(student1);
        Student student2 = new Student("Lorenzo", "Nuti");
        addTestStudentToDataBase(student2);
        Exam exam = new Exam("ATTSW", new ArrayList<>(Collections.singletonList(student1)));
        addTestExamToDataBase(exam);
        assertThatThrownBy(() -> examRepository.deleteReservation(exam, student2))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(entityManager.getTransaction().isActive()).isFalse();
        entityManager.refresh(exam);
        assertThat(exam.getStudents()).containsExactly(student1);
    }

    @Test
    public void deleteStudentReservationsFromDataBase() {
        Student student1 = new Student("Andrea", "Puccia");
        addTestStudentToDataBase(student1);
        Student student2 = new Student("Lorenzo", "Nuti");
        addTestStudentToDataBase(student2);
        Student student3 = new Student("Mario", "Rossi");
        addTestStudentToDataBase(student3);

        Exam exam1 = new Exam("ATTSW", new ArrayList<>(Arrays.asList(student1, student2)));
        addTestExamToDataBase(exam1);
        Exam exam2 = new Exam("Analisi", new ArrayList<>(Arrays.asList(student1, student3)));
        addTestExamToDataBase(exam2);

        examRepository.deleteStudentReservations(student1);
        entityManager.refresh(exam1);
        entityManager.refresh(exam2);
        assertThat(exam1.getStudents()).containsExactly(student2);
        assertThat(exam2.getStudents()).containsExactly(student3);
    }


    private void addTestExamToDataBase(Exam exam) {
        entityManager.getTransaction().begin();
        entityManager.persist(exam);
        entityManager.getTransaction().commit();
    }

    private void addTestStudentToDataBase(Student student) {
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();
    }

    private List<Exam> getExamsFromDataBase() {
        return entityManager.createQuery("select e from Exam e", Exam.class).getResultList();

    }
}

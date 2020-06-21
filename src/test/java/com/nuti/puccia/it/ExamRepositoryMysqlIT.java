package com.nuti.puccia.it;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.repository.mysql.ExamRepositoryMysql;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class ExamRepositoryMysqlIT {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    private ExamRepositoryMysql examRepository;

    private Student student1;
    private Student student2;
    private Student student3;

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

        student1 = new Student("Andrea", "Puccia");
        student2 = new Student("Lorenzo", "Nuti");
        student3 = new Student("Mario", "Rossi");
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
    public void findAllInOrderWhenDataBaseIsNotEmpty() {
        Exam exam1 = new Exam("ATTSW", new LinkedHashSet<>());
        addTestExamToDataBase(exam1);
        Exam exam2 = new Exam("Analisi", new LinkedHashSet<>());
        addTestExamToDataBase(exam2);
        assertThat(examRepository.findAll()).containsExactly(exam2, exam1);
    }

    @Test
    public void findAllRefreshExamStudents() {
        addTestStudentToDataBase(student1);
        addTestStudentToDataBase(student2);
        Exam exam1 = new Exam("ATTSW", new LinkedHashSet<>(Arrays.asList(student1, student2)));
        addTestExamToDataBase(exam1);
        assertThat(examRepository.findAll()).containsExactly(exam1);
        assertThat(exam1.getStudents()).containsExactly(student2, student1);
    }

    @Test
    public void addNewExamToDatabase() {
        Exam exam1 = new Exam("ATTSW", new LinkedHashSet<>());
        addTestExamToDataBase(exam1);
        Exam exam2 = new Exam("Analisi", new LinkedHashSet<>());
        entityManager.getTransaction().begin();
        examRepository.addExam(exam2);
        entityManager.getTransaction().commit();
        assertThat(getExamsFromDataBase()).containsExactlyInAnyOrder(exam1, exam2);
    }

    @Test
    public void deleteExamFromDataBase() {
        Exam exam1 = new Exam("ATTSW", new LinkedHashSet<>());
        addTestExamToDataBase(exam1);
        Exam exam2 = new Exam("Analisi", new LinkedHashSet<>());
        addTestExamToDataBase(exam2);
        entityManager.getTransaction().begin();
        examRepository.deleteExam(exam1);
        entityManager.getTransaction().commit();
        assertThat(getExamsFromDataBase()).containsExactly(exam2);
    }

    @Test
    public void addReservationToDataBase() {
        addTestStudentToDataBase(student1);
        addTestStudentToDataBase(student2);
        addTestStudentToDataBase(student3);

        Exam exam = new Exam("ATTSW", new LinkedHashSet<>(Arrays.asList(student2, student3)));
        addTestExamToDataBase(exam);
        entityManager.getTransaction().begin();
        examRepository.addReservation(exam, student1);
        entityManager.getTransaction().commit();
        entityManager.refresh(exam);
        assertThat(exam.getStudents()).containsExactly(student2, student1, student3);
    }

    @Test
    public void addExistingReservationToDataBase() {
        addTestStudentToDataBase(student1);
        Exam exam = new Exam("ATTSW", new LinkedHashSet<>(Collections.singletonList(student1)));
        addTestExamToDataBase(exam);
        entityManager.getTransaction().begin();
        assertThatThrownBy(() -> examRepository.addReservation(exam, student1)).isInstanceOf(Error.class);
        entityManager.getTransaction().commit();
        entityManager.refresh(exam);
        assertThat(exam.getStudents()).containsExactly(student1);
    }

    @Test
    public void deleteReservationFromDataBase() {
        addTestStudentToDataBase(student1);
        Exam exam = new Exam("ATTSW", new LinkedHashSet<>(Collections.singletonList(student1)));
        addTestExamToDataBase(exam);
        entityManager.getTransaction().begin();
        examRepository.deleteReservation(exam, student1);
        entityManager.getTransaction().commit();
        entityManager.refresh(exam);
        assertThat(exam.getStudents()).isEmpty();
    }

    @Test
    public void deleteReservationFromDataBaseWhenStudentNotPresent() {
        addTestStudentToDataBase(student1);
        addTestStudentToDataBase(student2);
        Exam exam = new Exam("ATTSW", new LinkedHashSet<>(Collections.singletonList(student1)));
        addTestExamToDataBase(exam);
        entityManager.getTransaction().begin();
        examRepository.deleteReservation(exam, student2);
        entityManager.getTransaction().commit();
        entityManager.refresh(exam);
        assertThat(exam.getStudents()).containsExactly(student1);
    }

    @Test
    public void deleteStudentReservationsFromDataBase() {
        addTestStudentToDataBase(student1);
        addTestStudentToDataBase(student2);
        addTestStudentToDataBase(student3);

        Exam exam1 = new Exam("ATTSW", new LinkedHashSet<>(Arrays.asList(student1, student2)));
        addTestExamToDataBase(exam1);
        Exam exam2 = new Exam("Analisi", new LinkedHashSet<>(Arrays.asList(student1, student3)));
        addTestExamToDataBase(exam2);

        entityManager.getTransaction().begin();
        examRepository.deleteStudentReservations(student1);
        entityManager.getTransaction().commit();
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

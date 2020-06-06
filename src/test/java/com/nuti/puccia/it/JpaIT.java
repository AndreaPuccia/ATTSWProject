package com.nuti.puccia.it;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JpaIT {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

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
    public void testStudent() {
        Student student = new Student("Andrea", "Puccia");
        assertThat(student.getId()).isEqualTo(0);
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();
        assertThat(student.getId()).isGreaterThan(0);

        List<Student> results = entityManager.createQuery("select s from Student s", Student.class).getResultList();
        assertThat(results).containsExactly(student);
    }

    @Test
    public void testExam() {
        Exam exam = new Exam("ATTSW", new ArrayList<>());
        assertThat(exam.getId()).isEqualTo(0);
        entityManager.getTransaction().begin();
        entityManager.persist(exam);
        entityManager.getTransaction().commit();
        assertThat(exam.getId()).isGreaterThan(0);

        List<Exam> results = entityManager.createQuery("select e from Exam e", Exam.class).getResultList();
        assertThat(results).containsExactly(exam);
    }

    @Test
    public void testExamStudents() {
        Exam exam = new Exam("ATTSW", new ArrayList<>());
        Student student1 = new Student("Andrea", "Puccia");
        Student student2 = new Student("Lorenzo", "Nuti");
        Student student3 = new Student("Mario", "Rossi");
        exam.addStudent(student1);
        exam.addStudent(student2);
        exam.addStudent(student3);
        entityManager.getTransaction().begin();
        entityManager.persist(exam);
        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.persist(student3);
        entityManager.getTransaction().commit();

        entityManager.refresh(exam);
        assertThat(exam.getStudents()).containsExactly(student2, student1, student3);
    }
}

package com.nuti.puccia.it;

import com.nuti.puccia.model.Student;
import com.nuti.puccia.repository.mysql.StudentRepositoryMysql;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentRepositoryIT {
    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    private StudentRepositoryMysql studentRepository;

    @BeforeClass
    public static void setUpClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory("TESTS");
    }

    @AfterClass
    public static void tearDownClass() {
        entityManagerFactory.close();
    }

    @Before
    public void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM Exam e").executeUpdate();
        entityManager.createQuery("DELETE FROM Student s").executeUpdate();
        entityManager.getTransaction().commit();

        studentRepository = new StudentRepositoryMysql(entityManager);
    }

    @After
    public void tearDown() {
        entityManager.close();
    }

    @Test
    public void findAllWhenDataBaseIsEmpty() {
        assertThat(studentRepository.findAll()).isEmpty();
    }

    @Test
    public void findAllWhenDataBaseIsNotEmpty() {
        Student student = new Student("Andrea", "Puccia");
        addTestStudentToDataBase(student);
        assertThat(studentRepository.findAll()).containsExactly(student);
    }

    @Test
    public void findAllInOrderWhenDataBaseIsNotEmpty() {
        Student student1 = new Student("Andrea", "Puccia");
        addTestStudentToDataBase(student1);
        Student student2 = new Student("Lorenzo", "Nuti");
        addTestStudentToDataBase(student2);
        assertThat(studentRepository.findAll()).containsExactly(student2, student1);
    }

    @Test
    public void addNewStudentToDatabase() {
        Student student1 = new Student("Andrea", "Puccia");
        addTestStudentToDataBase(student1);
        Student student2 = new Student("Lorenzo", "Nuti");
        studentRepository.addStudent(student2);
        assertThat(getStudentFromDataBase()).contains(student1, student2);
        assertThat(entityManager.getTransaction().isActive()).isFalse();
    }

    @Test
    public void deleteStudentFromDataBase() {
        Student student1 = new Student("Andrea", "Puccia");
        addTestStudentToDataBase(student1);
        studentRepository.deleteStudent(student1);
        assertThat(getStudentFromDataBase()).isEmpty();
        assertThat(entityManager.getTransaction().isActive()).isFalse();
    }

    @Test
    public void findByIdAStudentWhenItDoesNotExist() {
        Student student = new Student("Andrea", "Puccia");
        addTestStudentToDataBase(student);
        assertThat(studentRepository.findById(0)).isNull();
    }

    @Test
    public void findByIdAStudentWhenItExists() {
        Student student1 = new Student("Andrea", "Puccia");
        addTestStudentToDataBase(student1);
        Student student2 = new Student("Lorenzo", "Nuti");
        addTestStudentToDataBase(student2);
        assertThat(studentRepository.findById(student1.getId())).isEqualTo(student1);
    }


    private void addTestStudentToDataBase(Student student) {
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();
    }


    private List<Student> getStudentFromDataBase() {
        return entityManager.createQuery("select s from Student s", Student.class).getResultList();

    }
}
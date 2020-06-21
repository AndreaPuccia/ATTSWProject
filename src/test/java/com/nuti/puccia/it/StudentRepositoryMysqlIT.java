package com.nuti.puccia.it;

import com.nuti.puccia.model.Student;
import com.nuti.puccia.repository.mysql.StudentRepositoryMysql;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StudentRepositoryMysqlIT {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    private StudentRepositoryMysql studentRepository;

    private Student student1;
    private Student student2;
    private Student student3;

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

        student1 = new Student("Andrea", "Puccia");
        student2 = new Student("Lorenzo", "Nuti");
        student3 = new Student("Mario", "Rossi");
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
    public void findAllInOrderWhenDataBaseIsNotEmpty() {
        addTestStudentToDataBase(student1);
        addTestStudentToDataBase(student2);
        addTestStudentToDataBase(student3);
        assertThat(studentRepository.findAll()).containsExactly(student2, student1, student3);
    }

    @Test
    public void addNewStudentToDatabase() {
        addTestStudentToDataBase(student1);
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student2);
        entityManager.getTransaction().commit();
        assertThat(getStudentFromDataBase()).containsExactlyInAnyOrder(student1, student2);
    }

    @Test
    public void deleteStudentFromDataBase() {
        addTestStudentToDataBase(student1);
        addTestStudentToDataBase(student2);
        entityManager.getTransaction().begin();
        studentRepository.deleteStudent(student1);
        entityManager.getTransaction().commit();
        assertThat(getStudentFromDataBase()).containsExactly(student2);
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

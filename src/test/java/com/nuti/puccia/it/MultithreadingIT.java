package com.nuti.puccia.it;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.repository.ExamRepository;
import com.nuti.puccia.repository.StudentRepository;
import com.nuti.puccia.repository.mysql.ExamRepositoryMysql;
import com.nuti.puccia.repository.mysql.StudentRepositoryMysql;
import com.nuti.puccia.service_layer.ServiceLayer;
import com.nuti.puccia.transaction_manager.mysql.TransactionManagerMysql;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class MultithreadingIT {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    private StudentRepository studentRepository;
    private ExamRepository examRepository;

    private Student student1;
    private Student student2;
    private Exam exam1;
    private Exam exam2;

    private final int nThreads = 10;

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
        studentRepository = new StudentRepositoryMysql(entityManager);

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
    public void deleteStudentConcurrent() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addExam(exam2);
        examRepository.addReservation(exam1, student1);
        examRepository.addReservation(exam2, student1);
        examRepository.addReservation(exam2, student2);
        entityManager.getTransaction().commit();

        List<EntityManager> entityManagerList = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<ServiceLayer> serviceLayerList = new ArrayList<>();

        for (int i = 0; i < nThreads; i++) {
            EntityManager em = entityManagerFactory.createEntityManager();
            entityManagerList.add(em);
            serviceLayerList.add(new ServiceLayer(new TransactionManagerMysql(em)));
            students.add(em.find(Student.class, student1.getId()));
        }
        List<Thread> threads = IntStream.range(0, nThreads).mapToObj(i -> new Thread(() -> {
            try {
                serviceLayerList.get(i).deleteStudent(students.get(i));
            } catch (Throwable ignored) {
            }
            entityManagerList.get(i).close();
        })).peek(Thread::start).collect(Collectors.toList());
        await().atMost(10, SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

        assertThat(studentRepository.findAll()).containsExactly(student2);
        assertThat(examRepository.findAll()).containsExactlyInAnyOrder(exam1, exam2);
        entityManager.refresh(exam1);
        assertThat(exam1.getStudents()).isEmpty();
        entityManager.refresh(exam2);
        assertThat(exam2.getStudents()).containsExactly(student2);
    }

    @Test
    public void deleteExamAndStudentConcurrent() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addExam(exam2);
        examRepository.addReservation(exam1, student1);
        examRepository.addReservation(exam2, student1);
        examRepository.addReservation(exam2, student2);
        entityManager.getTransaction().commit();

        List<EntityManager> entityManagerList = new ArrayList<>();
        List<Exam> exams = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<ServiceLayer> serviceLayerList = new ArrayList<>();

        for (int i = 0; i < nThreads; i++) {
            EntityManager em = entityManagerFactory.createEntityManager();
            entityManagerList.add(em);
            serviceLayerList.add(new ServiceLayer(new TransactionManagerMysql(em)));
            exams.add(em.find(Exam.class, exam1.getId()));
            students.add(em.find(Student.class, student1.getId()));
        }
        List<Thread> threads = IntStream.range(0, nThreads).mapToObj(i -> new Thread(() -> {
            try {
                if (i % 2 == 0)
                    serviceLayerList.get(i).deleteExam(exams.get(i));
                else
                    serviceLayerList.get(i).deleteStudent(students.get(i));
            } catch (Throwable ignored) {
            }
            entityManagerList.get(i).close();

        })).peek(Thread::start).collect(Collectors.toList());
        await().atMost(10, SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));
        assertThat(studentRepository.findAll()).containsExactly(student2);
        assertThat(examRepository.findAll()).containsExactly(exam2);
        entityManager.refresh(exam2);
        assertThat(exam2.getStudents()).containsExactly(student2);
    }

    @Test
    public void deleteStudentAndAddReservationConcurrent() {
        entityManager.getTransaction().begin();
        studentRepository.addStudent(student1);
        studentRepository.addStudent(student2);
        examRepository.addExam(exam1);
        examRepository.addExam(exam2);
        examRepository.addReservation(exam1, student2);
        examRepository.addReservation(exam2, student1);
        examRepository.addReservation(exam2, student2);
        entityManager.getTransaction().commit();

        List<EntityManager> entityManagerList = new ArrayList<>();
        List<Exam> exams = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<ServiceLayer> serviceLayerList = new ArrayList<>();

        for (int i = 0; i < nThreads; i++) {
            EntityManager em = entityManagerFactory.createEntityManager();
            entityManagerList.add(em);
            serviceLayerList.add(new ServiceLayer(new TransactionManagerMysql(em)));
            exams.add(em.find(Exam.class, exam1.getId()));
            students.add(em.find(Student.class, student1.getId()));
        }
        List<Thread> threads = IntStream.range(0, nThreads).mapToObj(i -> new Thread(() -> {
            try {
                if (i % 2 == 0)
                    serviceLayerList.get(i).addReservation(exams.get(i), students.get(i));
                else
                    serviceLayerList.get(i).deleteStudent(students.get(i));
            } catch (Throwable ignored) {
            }
            entityManagerList.get(i).close();
        })).peek(Thread::start).collect(Collectors.toList());
        await().atMost(10, SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

        assertThat(studentRepository.findAll()).containsExactly(student2);
        assertThat(examRepository.findAll()).containsExactlyInAnyOrder(exam1, exam2);
        entityManager.refresh(exam1);
        assertThat(exam1.getStudents()).containsExactly(student2);
        entityManager.refresh(exam2);
        assertThat(exam2.getStudents()).containsExactly(student2);
    }
}

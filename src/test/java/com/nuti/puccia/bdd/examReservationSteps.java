package com.nuti.puccia.bdd;

import com.nuti.puccia.App;
import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.jbehave.core.annotations.*;

import javax.persistence.EntityManager;
import javax.swing.*;

import java.util.*;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

public class examReservationSteps {

    private EntityManager entityManager;
    private FrameFixture window;

    private final String selectedName = "Andrea";
    private final String selectedSurname = "Puccia";
    private final String reservationName = "Lorenzo";
    private final String reservationSurname = "Nuti";
    private final String newName = "Mario";
    private final String newSurname = "Rossi";

    private final String selectedExam = "ATTSW";
    private final String otherExam = "Analisi";
    private final String newExam = "Fisica";


    @BeforeStories
    public void setUpStories() {
        Map<String, String> settings = new HashMap<>();
        settings.put("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/attsw");
        settings.put("javax.persistence.jdbc.user", "root");
        settings.put("javax.persistence.jdbc.password", "");
        entityManager = App.getEntityManager(settings);
    }

    @BeforeScenario
    public void setUp() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("DELETE FROM Exam e").executeUpdate();
        entityManager.createQuery("DELETE FROM Student s").executeUpdate();
        entityManager.getTransaction().commit();
    }

    @AfterScenario
    public void tearDown() {
        if (window != null) {
            window.cleanUp();
        }
    }

    @AfterStories
    public void tearDownStories() {
        App.closeConnection();
    }

    @Given("The Database contains few students and few exams")
    public void givenDatabaseContainsFewStudentsAndFewExams() {
        Student selectedStudent = new Student(selectedName, selectedSurname);
        Student reservationStudent = new Student(reservationName, reservationSurname);
        addStudentToDataBase(selectedStudent);
        addStudentToDataBase(reservationStudent);
        addExamToDataBase(new Exam(selectedExam, new LinkedHashSet<>(Collections.singletonList(reservationStudent))));
        addExamToDataBase(new Exam(otherExam, new LinkedHashSet<>()));
    }

    @Given("The Exam Reservation View is shown")
    public void givenExamReservationViewIsShown() {
        application("com.nuti.puccia.App").start();
        window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
            @Override
            protected boolean isMatching(JFrame frame) {
                return "Exam Reservations".equals(frame.getTitle()) && frame.isShowing();
            }
        }).using(BasicRobot.robotWithCurrentAwtHierarchy());
    }

    @When("The user clicks $buttonName button")
    public void whenTheUserClicksAButton(String buttonName) {
        window.button(buttonName).click();
    }

    // Exam

    @Given("The user enter exam name")
    public void givenTheUserEnterExamName() {
        window.textBox("ExamNameText").enterText(newExam);
    }
    @When("The user selects an Exam")
    @Given("The user selects an Exam")
    public void theUserSelectsAnExam() {
        window.list("ExamList").selectItem(Pattern.compile(".*" + selectedExam + ".*"));
    }

    @Then("Exam list contains exams info")
    public void thenExamListContainsExamsInfo() {
        assertThat(window.list("ExamList").contents())
                .anySatisfy(e -> assertThat(e).contains(selectedExam))
                .anySatisfy(e -> assertThat(e).contains(otherExam));
    }

    @Then("Exam list contains new exam info")
    public void thenExamListContainsNewExamInfo() {
        assertThat(window.list("ExamList").contents())
                .anySatisfy(e -> assertThat(e).contains(newExam));
    }

    @Then("Exam list does not contain selected exam info")
    public void thenExamListDoesNotContainSelectedExamInfo() {
        assertThat(window.list("ExamList").contents())
                .noneMatch(e -> e.contains(selectedExam));
    }

    // Reservation

    @Then("Reservation list contains reservations info")
    public void thenReservationListContainsReservationsInfo() {
        assertThat(window.list("ReservationList").contents())
                .anySatisfy(e -> assertThat(e).contains(reservationName, reservationSurname));
    }
    @Then("Reservation list contains selected student info")
    public void thenReservationListContainsSelectedStudentInfo() {
        assertThat(window.list("ReservationList").contents())
                .anySatisfy(e -> assertThat(e).contains(selectedName, selectedSurname));
    }

    @Given("The user selects a Reservation")
    public void givenTheUserSelectsAReservation() {
        window.list("ReservationList").selectItem(Pattern.compile(".*" + reservationSurname + " " + reservationName + ".*"));
    }

    @Then("Reservation list does not contain selected reservation info")
    public void thenReservationListDoesNotContainSelectedReservationInfo() {
        assertThat(window.list("ReservationList").contents())
                .noneMatch(e -> e.contains(reservationSurname + " " + reservationName));
    }

    // Student

    @Given("The user enter student name and surname")
    public void givenTheUserEnterStudentNameAndSurname() {
        window.textBox("StudentNameText").enterText(newName);
        window.textBox("StudentSurnameText").enterText(newSurname);
    }
    @Given("The user selects a Student")
    public void givenTheUserSelectsAStudent() {
        window.list("StudentList").selectItem(Pattern.compile(".*" + selectedSurname + " " + selectedName + ".*"));
    }

    @Then("Student list contains students info")
    public void thenStudentListContainsStudentsInfo() {
        assertThat(window.list("StudentList").contents())
                .anySatisfy(e -> assertThat(e).contains(selectedName, selectedSurname))
                .anySatisfy(e -> assertThat(e).contains(reservationName, reservationSurname));
    }

    @Then("Student list contains new student info")
    public void thenStudentListContainsNewStudentInfo() {
        assertThat(window.list("StudentList").contents())
                .anySatisfy(e -> assertThat(e).contains(newName, newSurname));
    }

    @Then("Student list does not contain selected student info")
    public void thenStudentListDoesNotContainSelectedStudentInfo() {
        assertThat(window.list("StudentList").contents())
                .noneMatch(e -> e.contains(selectedSurname + " " + selectedName));
    }

    @Given("The user selects Student that is also in reservations list")
    public void givenTheUserSelectsStudentThatIsAlsoInReservationsList() {
        window.list("StudentList").selectItem(Pattern.compile(".*" + reservationSurname + " " + reservationName + ".*"));
    }

    @Then("Error student already present is shown")
    public void thenErrorStudentAlreadyPresentIsShown() {
        assertThat(window.label("ErrorLabel").text()).containsPattern(Pattern.compile(".*" + reservationSurname + " " + reservationName + ".*" + "already present" + ".*"));

    }


    private void addStudentToDataBase(Student student) {
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();
    }

    private void addExamToDataBase(Exam exam) {
        entityManager.getTransaction().begin();
        entityManager.persist(exam);
        entityManager.getTransaction().commit();
    }
}

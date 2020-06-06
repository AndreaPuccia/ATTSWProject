package com.nuti.puccia.repository.mysql;

import com.nuti.puccia.model.Exam;
import com.nuti.puccia.model.Student;
import com.nuti.puccia.repository.ExamRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class ExamRepositoryMysql implements ExamRepository {
    EntityManager entityManager;

    public ExamRepositoryMysql(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void deleteExam(Exam exam) {
        entityManager.getTransaction().begin();
        entityManager.remove(exam);
        entityManager.getTransaction().commit();
    }

    @Override
    public void addExam(Exam exam) {
        entityManager.getTransaction().begin();
        entityManager.persist(exam);
        entityManager.getTransaction().commit();
    }

    @Override
    public void addReservation(Exam exam, Student student) {
        try {
            entityManager.getTransaction().begin();
            exam.addStudent(student);
        } finally {
            entityManager.getTransaction().commit();
            entityManager.refresh(exam);
        }
    }

    @Override
    public void deleteReservation(Exam exam, Student student) {
        try {
            entityManager.getTransaction().begin();
            exam.removeStudent(student);
        } finally {
            entityManager.getTransaction().commit();
        }
    }

    @Override
    public void deleteStudentReservations(Student student) {
        TypedQuery<Exam> query = entityManager.createQuery("select e from Exam e where :student member of e.students", Exam.class);
        query.setParameter("student", student);
        List<Exam> exams = query.getResultList();
        entityManager.getTransaction().begin();
        exams.forEach(exam -> exam.removeStudent(student));
        entityManager.getTransaction().commit();
    }

    @Override
    public List<Exam> findAll() {
        return entityManager.createQuery("select e from Exam e order by e.name", Exam.class).getResultList();
    }

    @Override
    public Exam findById(long id) {
        return entityManager.find(Exam.class, id);
    }
}

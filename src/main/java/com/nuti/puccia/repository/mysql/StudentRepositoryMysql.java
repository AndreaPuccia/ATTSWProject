package com.nuti.puccia.repository.mysql;

import com.nuti.puccia.model.Student;
import com.nuti.puccia.repository.StudentRepository;

import javax.persistence.EntityManager;
import java.util.List;

public class StudentRepositoryMysql implements StudentRepository {
    EntityManager entityManager;

    public StudentRepositoryMysql(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void deleteStudent(Student student) {
        entityManager.getTransaction().begin();
        entityManager.remove(student);
        entityManager.getTransaction().commit();
    }

    @Override
    public void addStudent(Student student) {
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();

    }

    @Override
    public List<Student> findAll() {
        return entityManager.createQuery("select s from Student s order by s.surname, s.name", Student.class).getResultList();
    }

    @Override
    public Student findById(long id) {
        return entityManager.find(Student.class, id);
    }
}

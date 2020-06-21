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
        entityManager.remove(student);
    }

    @Override
    public void addStudent(Student student) {
        entityManager.persist(student);
    }

    @Override
    public List<Student> findAll() {
        return entityManager.createQuery("select s from Student s order by s.surname, s.name", Student.class).getResultList();
    }
}

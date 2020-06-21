package com.nuti.puccia.transaction_manager.mysql;

import com.nuti.puccia.repository.mysql.ExamRepositoryMysql;
import com.nuti.puccia.repository.mysql.StudentRepositoryMysql;
import com.nuti.puccia.transaction_manager.TransactionFunction;
import com.nuti.puccia.transaction_manager.TransactionManager;

import javax.persistence.EntityManager;

public class TransactionManagerMysql implements TransactionManager {
    EntityManager entityManager;

    public TransactionManagerMysql(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <T> T executeTransaction(TransactionFunction<T> code) {
        return executeTransaction(code, "Transaction rolled back");
    }

    @Override
    public <T> T executeTransaction(TransactionFunction<T> code, String errorMessage) {
        try {
            entityManager.getTransaction().begin();
            T result = code.apply(new ExamRepositoryMysql(entityManager), new StudentRepositoryMysql(entityManager));
            entityManager.getTransaction().commit();
            return result;
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new IllegalArgumentException(errorMessage);
        }
    }

}

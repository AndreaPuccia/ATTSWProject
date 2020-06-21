package com.nuti.puccia.transaction_manager;

public interface TransactionManager {
    <T> T executeTransaction(TransactionFunction<T> code);

    <T> T executeTransaction(TransactionFunction<T> code, String errorMessage);
}

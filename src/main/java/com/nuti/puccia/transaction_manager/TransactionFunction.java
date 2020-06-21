package com.nuti.puccia.transaction_manager;

import com.nuti.puccia.repository.ExamRepository;
import com.nuti.puccia.repository.StudentRepository;

import java.util.function.BiFunction;

@FunctionalInterface
public interface TransactionFunction<T> extends BiFunction<ExamRepository, StudentRepository, T> {
}


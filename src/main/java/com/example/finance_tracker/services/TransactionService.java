package com.example.finance_tracker.services;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.models.Transaction;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    Transaction create(Transaction transaction);

    Transaction update(Transaction transaction);

    void delete(Long transactionId, Long userId);

    List<Transaction> getByUser(Long userId);

    List<Transaction> getByType(Long userId, Type type);

    List<Transaction> getByCategory(Long userId, Long categoryId);

    List<Transaction> getByDateRange(Long userId, LocalDate after, LocalDate before);
}

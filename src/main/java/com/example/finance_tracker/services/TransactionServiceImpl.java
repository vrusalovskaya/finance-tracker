package com.example.finance_tracker.services;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.entities.CategoryEntity;
import com.example.finance_tracker.entities.TransactionEntity;
import com.example.finance_tracker.entities.UserEntity;
import com.example.finance_tracker.exceptions.AccessDeniedException;
import com.example.finance_tracker.exceptions.ResourceNotFoundException;
import com.example.finance_tracker.exceptions.ValidationException;
import com.example.finance_tracker.mappers.TransactionMapper;
import com.example.finance_tracker.models.Transaction;
import com.example.finance_tracker.repositories.CategoryRepository;
import com.example.finance_tracker.repositories.TransactionRepository;
import com.example.finance_tracker.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public Transaction create(Transaction transaction) {
        validateAmount(transaction.getAmount());
        validateDate(transaction.getDate());
        CategoryEntity categoryEntity = findCategoryInDb(transaction.getCategoryId());
        if (transaction.getUserId() == null) {
            throw new ValidationException("Transaction must be assigned to user");
        }
        UserEntity userEntity = findUserInDb(transaction.getUserId());

        if (!categoryEntity.getUserEntity().getId().equals(transaction.getUserId())) {
            throw new ValidationException("Category does not belong to user");
        }

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setType(transaction.getType());
        transactionEntity.setAmount(transaction.getAmount());
        transactionEntity.setDate(transaction.getDate());
        transactionEntity.setDescription(transaction.getDescription());
        transactionEntity.setCategoryEntity(categoryEntity);
        transactionEntity.setUserEntity(userEntity);

        TransactionEntity savedTransaction = transactionRepository.save(transactionEntity);
        return transactionMapper.toModel(savedTransaction);
    }

    @Override
    @Transactional
    public Transaction update(Transaction transaction) {
        if (transaction.getUserId() == null) {
            throw new ValidationException("Transaction must be assigned to user");
        }

        TransactionEntity transactionEntity = findTransactionInDb(transaction.getId());

        if (!transactionEntity.getUserEntity().getId().equals(transaction.getUserId())) {
            throw new AccessDeniedException("Access denied");
        }

        validateAmount(transaction.getAmount());
        validateDate(transaction.getDate());
        CategoryEntity categoryEntity = null;
        if (transaction.getCategoryId() != null) {
            categoryEntity = findCategoryInDb(transaction.getCategoryId());

            if (!categoryEntity.getUserEntity().getId().equals(transaction.getUserId())) {
                throw new ValidationException("Category does not belong to user");
            }
        }

        transactionEntity.setType(transaction.getType());
        transactionEntity.setAmount(transaction.getAmount());
        transactionEntity.setDate(transaction.getDate());
        transactionEntity.setDescription(transaction.getDescription());
        transactionEntity.setCategoryEntity(categoryEntity);

        return transactionMapper.toModel(transactionEntity);
    }

    @Override
    @Transactional
    public void delete(Long transactionId, Long userId) {
        TransactionEntity transactionEntity = findTransactionInDb(transactionId);

        if (!transactionEntity.getUserEntity().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        transactionRepository.deleteById(transactionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getByUser(Long userId) {
        return transactionRepository.findByUserEntityId(userId).stream()
                .map(transactionMapper::toModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getByType(Long userId, Type type) {
        return transactionRepository.findByUserEntityIdAndType(userId, type).stream()
                .map(transactionMapper::toModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getByCategory(Long userId, Long categoryId) {
        return transactionRepository.findByUserEntityIdAndCategoryEntityId(userId, categoryId)
                .stream().map(transactionMapper::toModel).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getByDateRange(Long userId, LocalDate after, LocalDate before) {
        validateDates(after, before);
        return transactionRepository.findByUserAndDateRange(userId, after, before)
                .stream().map(transactionMapper::toModel).toList();
    }

    private UserEntity findUserInDb(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " was not found"));
    }

    private CategoryEntity findCategoryInDb(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + categoryId + " was not found"));
    }

    private TransactionEntity findTransactionInDb(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id " + transactionId + " was not found"));

    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount of transaction should be positive");
        }
    }

    private void validateDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new ValidationException("Setting future dates for transactions is not allowed");
        }
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Start date for filtration cannot be after end date");
        }
    }
}

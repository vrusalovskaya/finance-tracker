package com.example.finance_tracker.services;

import com.example.finance_tracker.entities.CategoryEntity;
import com.example.finance_tracker.entities.TransactionEntity;
import com.example.finance_tracker.entities.UserEntity;
import com.example.finance_tracker.exceptions.ResourceNotFoundException;
import com.example.finance_tracker.exceptions.ValidationException;
import com.example.finance_tracker.mappers.TransactionMapper;
import com.example.finance_tracker.models.Transaction;
import com.example.finance_tracker.repositories.CategoryRepository;
import com.example.finance_tracker.repositories.TransactionRepository;
import com.example.finance_tracker.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    TransactionRepository transactionRepository;
    @Mock
    CategoryRepository categoryRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    TransactionMapper transactionMapper;

    @InjectMocks
    TransactionServiceImpl transactionService;

    @Test
    void create_shouldThrow_WhenAmountIsZero() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ZERO);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> transactionService.create(transaction));

        assertTrue(ex.getMessage().contains("Amount of transaction should be positive"));
    }

    @Test
    void create_shouldThrow_WhenAmountIsNegative() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(-1));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> transactionService.create(transaction));

        assertTrue(ex.getMessage().contains("Amount of transaction should be positive"));
    }

    @Test
    void create_shouldThrow_WhenDateInFuture() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ONE);
        transaction.setDate(LocalDate.now().plusDays(1));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> transactionService.create(transaction));

        assertTrue(ex.getMessage().contains("Setting future dates for transactions is not allowed"));
    }

    @Test
    void create_shouldThrow_WhenCategoryNotFound() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ONE);
        transaction.setDate(LocalDate.now().minusDays(1));
        transaction.setCategoryId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.create(transaction));

        assertTrue(ex.getMessage().contains("Category with id 1 was not found"));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_WhenUserIdIsNull() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ONE);
        transaction.setDate(LocalDate.now().minusDays(1));
        transaction.setCategoryId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new CategoryEntity()));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> transactionService.create(transaction));

        assertTrue(ex.getMessage().contains("Transaction must be assigned to user"));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_WhenUserNotFound() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ONE);
        transaction.setDate(LocalDate.now().minusDays(1));
        transaction.setCategoryId(1L);
        transaction.setUserId(1L);

        when(categoryRepository.findById(1L)).thenReturn(
                Optional.of(new CategoryEntity()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.create(transaction));

        assertTrue(ex.getMessage().contains("User with id 1 was not found"));
    }

    @Test
    void create_shouldThrow_WhenCategoryDoesNotBelongToUser() {
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setId(1L);

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setId(2L);

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setUserEntity(userEntity1);

        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ONE);
        transaction.setDate(LocalDate.now().minusDays(1));
        transaction.setCategoryId(1L);
        transaction.setUserId(2L);

        when(categoryRepository.findById(1L)).thenReturn(
                Optional.of(categoryEntity));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userEntity2));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> transactionService.create(transaction));

        assertTrue(ex.getMessage().contains("Category does not belong to user"));
    }

    @Test
    void create_shouldPass_WhenValidTransaction() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setUserEntity(userEntity);

        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ONE);
        transaction.setDate(LocalDate.now().minusDays(1));
        transaction.setCategoryId(1L);
        transaction.setUserId(1L);

        when(categoryRepository.findById(1L)).thenReturn(
                Optional.of(categoryEntity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        TransactionEntity saved = new TransactionEntity();
        when(transactionRepository.save(any(TransactionEntity.class)))
                .thenReturn(saved);

        when(transactionMapper.toModel(saved)).thenReturn(new Transaction());

        assertDoesNotThrow(() -> transactionService.create(transaction));
        verify(transactionRepository).save(any(TransactionEntity.class));
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}
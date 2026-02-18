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
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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
        transaction.setUserId(1L);
        when(categoryRepository.findByIdAndUserEntityId(1L, 1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.create(transaction));

        assertTrue(ex.getMessage().contains("Category with id 1 was not found for user with id 1"));
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_WhenUserNotFound() {
        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.ONE);
        transaction.setDate(LocalDate.now().minusDays(1));
        transaction.setCategoryId(1L);
        transaction.setUserId(1L);

        when(categoryRepository.findByIdAndUserEntityId(1L, 1L)).thenReturn(
                Optional.of(new CategoryEntity()));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> transactionService.create(transaction));

        assertTrue(ex.getMessage().contains("User with id 1 was not found"));
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

        when(categoryRepository.findByIdAndUserEntityId(1L, 1L)).thenReturn(
                Optional.of(categoryEntity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        TransactionEntity saved = new TransactionEntity();
        when(transactionRepository.save(any(TransactionEntity.class)))
                .thenReturn(saved);

        when(transactionMapper.toModel(saved)).thenReturn(new Transaction());

        assertDoesNotThrow(() -> transactionService.create(transaction));
        verify(transactionRepository).save(any(TransactionEntity.class));
    }
}
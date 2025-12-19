package com.example.finance_tracker.controllers;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.dtos.TransactionRequest;
import com.example.finance_tracker.dtos.TransactionResponse;
import com.example.finance_tracker.mappers.TransactionMapper;
import com.example.finance_tracker.models.Transaction;
import com.example.finance_tracker.security.CurrentUserProvider;
import com.example.finance_tracker.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final CurrentUserProvider currentUser;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest transactionRequest) {
        Long userId = currentUser.getCurrentUserId();

        Transaction transaction = transactionMapper.toModel(transactionRequest);
        transaction.setUserId(userId);
        Transaction createdTransaction = transactionService.create(transaction);

        TransactionResponse transactionResponse = transactionMapper.toResponse(createdTransaction);

        URI location = URI.create(String.format("/api/v1/transactions/%d", transactionResponse.id()));
        return ResponseEntity.created(location).body(transactionResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(@PathVariable Long id, @Valid @RequestBody TransactionRequest transactionRequest) {
        Long userId = currentUser.getCurrentUserId();

        Transaction transaction = transactionMapper.toModel(transactionRequest);
        transaction.setId(id);
        transaction.setUserId(userId);
        Transaction updatedTransaction = transactionService.update(transaction);

        TransactionResponse transactionResponse = transactionMapper.toResponse(updatedTransaction);
        return ResponseEntity.ok(transactionResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = currentUser.getCurrentUserId();
        transactionService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<TransactionResponse> get() {
        Long userId = currentUser.getCurrentUserId();
        return transactionService.getByUser(userId)
                .stream().map(transactionMapper::toResponse).toList();
    }

    @GetMapping("/type/{type}")
    public List<TransactionResponse> getByType(@PathVariable String type) {
        Long userId = currentUser.getCurrentUserId();
        Type parsedType = Type.from(type);
        return transactionService.getByType(userId, parsedType)
                .stream().map(transactionMapper::toResponse).toList();
    }

    @GetMapping("/category/{categoryId}")
    public List<TransactionResponse> getByCategory(@PathVariable Long categoryId) {
        Long userId = currentUser.getCurrentUserId();
        return transactionService.getByCategory(userId, categoryId)
                .stream().map(transactionMapper::toResponse).toList();
    }

    @GetMapping("/dates")
    public List<TransactionResponse> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = currentUser.getCurrentUserId();
        return transactionService.getByDateRange(userId, startDate, endDate)
                .stream().map(transactionMapper::toResponse).toList();
    }
}

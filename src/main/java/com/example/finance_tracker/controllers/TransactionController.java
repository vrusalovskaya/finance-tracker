package com.example.finance_tracker.controllers;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.dtos.TransactionRequest;
import com.example.finance_tracker.dtos.TransactionResponse;
import com.example.finance_tracker.mappers.TransactionMapper;
import com.example.finance_tracker.models.Transaction;
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

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest transactionRequest) {
        Transaction transaction = transactionMapper.toModel(transactionRequest);
        Transaction createdTransaction = transactionService.create(transaction);

        TransactionResponse transactionResponse = transactionMapper.toResponse(createdTransaction);

        URI location = URI.create(String.format("/api/v1/transactions/%d", transactionResponse.id()));
        return ResponseEntity.created(location).body(transactionResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(@PathVariable Long id, @Valid @RequestBody TransactionRequest transactionRequest) {
        Transaction transaction = transactionMapper.toModel(transactionRequest);
        transaction.setId(id);
        Transaction updatedTransaction = transactionService.update(transaction);

        TransactionResponse transactionResponse = transactionMapper.toResponse(updatedTransaction);
        return ResponseEntity.ok(transactionResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
        transactionService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<TransactionResponse> getByUser(@RequestParam Long userId) {
        return transactionService.getByUser(userId)
                .stream().map(transactionMapper::toResponse).toList();
    }

    @GetMapping("/type/{type}")
    public List<TransactionResponse> getByTypeAndUser(@PathVariable String type,
                                                      @RequestParam Long userId) {
        Type parsedType = Type.from(type);
        return transactionService.getByType(userId, parsedType)
                .stream().map(transactionMapper::toResponse).toList();
    }

    @GetMapping("/category/{categoryId}")
    public List<TransactionResponse> getByCategoryAndUser(@PathVariable Long categoryId,
                                                          @RequestParam Long userId) {
        return transactionService.getByCategory(userId, categoryId)
                .stream().map(transactionMapper::toResponse).toList();
    }

    @GetMapping("/dates")
    public List<TransactionResponse> getByDateRangeAndUser(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return transactionService.getByDateRange(userId, startDate, endDate)
                .stream().map(transactionMapper::toResponse).toList();
    }
}

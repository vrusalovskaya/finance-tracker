package com.example.finance_tracker.dtos;

import com.example.finance_tracker.common.Type;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull(message = "Transaction type is mandatory")
        Type type,

        @NotNull(message = "Amount is mandatory")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,

        @NotNull(message = "Transaction date is mandatory")
        @PastOrPresent(message = "Transaction date cannot be in the future")
        LocalDate date,

        @Size(max = 255, message = "Description length should not exceed 255 characters")
        String description,

        Long categoryId,

        @NotNull(message = "User reference is mandatory")
        Long userId
) {
}

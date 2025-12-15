package com.example.finance_tracker.dtos;

import com.example.finance_tracker.common.Type;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        Type type,
        BigDecimal amount,
        LocalDate date,
        String description,
        Long categoryId,
        Long userId
) {
}

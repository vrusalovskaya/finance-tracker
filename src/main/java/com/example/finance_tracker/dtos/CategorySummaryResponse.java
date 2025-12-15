package com.example.finance_tracker.dtos;

import java.math.BigDecimal;

public record CategorySummaryResponse(
        Long categoryId,
        String categoryName,
        BigDecimal total
) {
}

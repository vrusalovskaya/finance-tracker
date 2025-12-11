package com.example.finance_tracker.projections;

import java.math.BigDecimal;

public record CategorySummaryProjection(
        Long categoryId,
        String categoryName,
        BigDecimal total) {
}

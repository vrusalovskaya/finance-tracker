package com.example.finance_tracker.models;

import java.math.BigDecimal;

public record CategorySummary(
        Long categoryId,
        String categoryName,
        BigDecimal total
) {}

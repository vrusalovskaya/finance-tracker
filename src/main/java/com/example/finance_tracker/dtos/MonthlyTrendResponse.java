package com.example.finance_tracker.dtos;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyTrendResponse(
        YearMonth month,
        BigDecimal total
) {
}

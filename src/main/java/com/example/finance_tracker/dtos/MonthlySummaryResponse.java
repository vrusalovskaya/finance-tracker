package com.example.finance_tracker.dtos;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlySummaryResponse(
        YearMonth month,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance
) {
}

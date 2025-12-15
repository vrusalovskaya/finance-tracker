package com.example.finance_tracker.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PeriodSummaryResponse(
        LocalDate from,
        LocalDate to,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance
) {
}

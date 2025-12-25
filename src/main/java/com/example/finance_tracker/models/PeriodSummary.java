package com.example.finance_tracker.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PeriodSummary(
        LocalDate from,
        LocalDate to,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance
) {
}

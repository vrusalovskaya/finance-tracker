package com.example.finance_tracker.models;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlySummary(
        YearMonth month,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal balance
) {}

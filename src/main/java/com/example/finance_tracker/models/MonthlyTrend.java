package com.example.finance_tracker.models;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyTrend(
        YearMonth month,
        BigDecimal total
) {}

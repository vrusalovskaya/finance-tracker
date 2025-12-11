package com.example.finance_tracker.projections;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
public class MonthlyTrendProjection {
    private final YearMonth yearMonth;
    private final BigDecimal total;

    public MonthlyTrendProjection(Long year, Long month, BigDecimal total) {
        this.yearMonth = YearMonth.of(year.intValue(), month.intValue());
        this.total = total;
    }
}

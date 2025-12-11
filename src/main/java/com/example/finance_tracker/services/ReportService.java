package com.example.finance_tracker.services;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.models.CategorySummary;
import com.example.finance_tracker.models.MonthlySummary;
import com.example.finance_tracker.models.MonthlyTrend;
import com.example.finance_tracker.models.PeriodSummary;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ReportService {
    MonthlySummary getMonthlySummary(Long userId, YearMonth month);

    List<CategorySummary> getMonthlySummaryByCategory(Long userId, YearMonth month, Type type);

    PeriodSummary getSummaryForPeriod(Long userId, LocalDate from, LocalDate to);

    List<MonthlyTrend> getMonthlyTrend(Long userId, YearMonth from, YearMonth to, Type type);
}

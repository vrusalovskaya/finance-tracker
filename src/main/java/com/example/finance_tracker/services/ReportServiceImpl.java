package com.example.finance_tracker.services;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.exceptions.ValidationException;
import com.example.finance_tracker.models.CategorySummary;
import com.example.finance_tracker.models.MonthlySummary;
import com.example.finance_tracker.models.MonthlyTrend;
import com.example.finance_tracker.models.PeriodSummary;
import com.example.finance_tracker.projections.MonthlyTrendProjection;
import com.example.finance_tracker.repositories.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    @Override
    @Transactional(readOnly = true)
    public MonthlySummary getMonthlySummary(Long userId, YearMonth month) {
        LocalDate from = month.atDay(1);
        LocalDate to = month.atEndOfMonth();

        BigDecimal income = reportRepository.sumByTypeAndDateRange(userId, Type.INCOME, from, to);
        BigDecimal expense = reportRepository.sumByTypeAndDateRange(userId, Type.EXPENSE, from, to);

        return new MonthlySummary(month, income, expense, income.subtract(expense));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySummary> getMonthlySummaryByCategory(Long userId, YearMonth month, Type type) {
        LocalDate from = month.atDay(1);
        LocalDate to = month.atEndOfMonth();

        return reportRepository.monthlySummaryByCategory(userId, type, from, to)
                .stream().map(categorySummaryProjection -> new CategorySummary(
                        categorySummaryProjection.categoryId(),
                        categorySummaryProjection.categoryName(),
                        categorySummaryProjection.total()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodSummary getSummaryForPeriod(Long userId, LocalDate from, LocalDate to) {
        validateDates(from, to);

        BigDecimal income = reportRepository.sumByTypeAndDateRange(userId, Type.INCOME, from, to);
        BigDecimal expense = reportRepository.sumByTypeAndDateRange(userId, Type.EXPENSE, from, to);

        return new PeriodSummary(from, to, income, expense, income.subtract(expense));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyTrend> getMonthlyTrend(Long userId, YearMonth from, YearMonth to, Type type) {
        validateMonths(from, to);

        List<MonthlyTrendProjection> raw = reportRepository
                .getMonthlyAggregates(userId, type, from.atDay(1), to.atEndOfMonth());

        Map<YearMonth, BigDecimal> map = raw.stream()
                .collect(Collectors.toMap(
                        MonthlyTrendProjection::getYearMonth,
                        MonthlyTrendProjection::getTotal
                ));

        List<MonthlyTrend> result = new ArrayList<>();
        YearMonth cursor = from;

        while (!cursor.isAfter(to)) {
            result.add(new MonthlyTrend(cursor, map.getOrDefault(cursor, BigDecimal.ZERO)));
            cursor = cursor.plusMonths(1);
        }

        return result;
    }

    private void validateDates(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new ValidationException("Start date is after end date");
        }
    }

    private void validateMonths(YearMonth from, YearMonth to) {
        if (from.isAfter(to)) {
            throw new ValidationException("Start month is after end month");
        }
    }
}

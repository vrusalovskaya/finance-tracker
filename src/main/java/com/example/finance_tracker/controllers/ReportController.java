package com.example.finance_tracker.controllers;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.dtos.CategorySummaryResponse;
import com.example.finance_tracker.dtos.MonthlySummaryResponse;
import com.example.finance_tracker.dtos.MonthlyTrendResponse;
import com.example.finance_tracker.dtos.PeriodSummaryResponse;
import com.example.finance_tracker.models.CategorySummary;
import com.example.finance_tracker.models.MonthlySummary;
import com.example.finance_tracker.models.MonthlyTrend;
import com.example.finance_tracker.models.PeriodSummary;
import com.example.finance_tracker.security.CurrentUserProvider;
import com.example.finance_tracker.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final CurrentUserProvider currentUser;

    @GetMapping("/monthly-summary")
    public MonthlySummaryResponse getMonthlySummary(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        Long userId = currentUser.getCurrentUserId();
        MonthlySummary monthlySummary = reportService.getMonthlySummary(userId, month);
        return new MonthlySummaryResponse(
                monthlySummary.month(),
                monthlySummary.income(),
                monthlySummary.expense(),
                monthlySummary.balance());
    }

    @GetMapping("/monthly-category-summary")
    public List<CategorySummaryResponse> getMonthlyCategorySummary(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
            @RequestParam String type
    ) {
        Long userId = currentUser.getCurrentUserId();
        Type parsedType = Type.from(type);
        return reportService.getMonthlySummaryByCategory(userId, month, parsedType)
                .stream().map(this::toResponse).toList();
    }

    @GetMapping("/period-summary")
    public PeriodSummaryResponse getPeriodSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Long userId = currentUser.getCurrentUserId();
        PeriodSummary periodSummary = reportService.getSummaryForPeriod(userId, startDate, endDate);
        return new PeriodSummaryResponse(periodSummary.from(), periodSummary.to(),
                periodSummary.income(), periodSummary.expense(), periodSummary.balance());
    }

    @GetMapping("/monthly-trend")
    public List<MonthlyTrendResponse> getMonthlyTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth to,
            @RequestParam String type
    ) {
        Long userId = currentUser.getCurrentUserId();
        Type parsedType = Type.from(type);
        return reportService.getMonthlyTrend(userId, from, to, parsedType)
                .stream().map(this::toResponse).toList();
    }

    private CategorySummaryResponse toResponse(CategorySummary categorySummary) {
        return new CategorySummaryResponse(
                categorySummary.categoryId(),
                categorySummary.categoryName(),
                categorySummary.total()
        );
    }

    private MonthlyTrendResponse toResponse(MonthlyTrend monthlyTrend) {
        return new MonthlyTrendResponse(monthlyTrend.month(), monthlyTrend.total());
    }
}



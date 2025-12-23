package com.example.finance_tracker.controllers;

import com.example.finance_tracker.BaseE2ETest;
import com.example.finance_tracker.common.Type;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ReportControllerTest extends BaseE2ETest {

    @Test
    void accessReports_whenJwtIsMissing_thenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/reports/monthly-summary")
                        .param("month", YearMonth.now().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getMonthlySummary_whenMixedDataExists_thenOnlyCurrentUserAndMonthAreCounted() throws Exception {
        String token = registerAndGetToken("monthly-robust@test.com");
        String otherUserToken = registerAndGetToken("monthly-other@test.com");

        Long income = createCategory(token, "Salary", Type.INCOME);
        Long expense = createCategory(token, "Food", Type.EXPENSE);

        Long otherUserCategory = createCategory(otherUserToken, "Other", Type.EXPENSE);

        createTransaction(token, income, 1000, Type.INCOME);
        createTransaction(token, expense, 200, Type.EXPENSE);

        createTransaction(otherUserToken, otherUserCategory, 999, Type.EXPENSE);

        mockMvc.perform(get("/api/v1/reports/monthly-summary")
                        .header("Authorization", "Bearer " + token)
                        .param("month", YearMonth.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(1000))
                .andExpect(jsonPath("$.expense").value(200))
                .andExpect(jsonPath("$.balance").value(800));
    }

    @Test
    void getMonthlySummary_whenNoTransactionsExist_thenReturnsZeros() throws Exception {
        String token = registerAndGetToken("monthly-empty@test.com");

        mockMvc.perform(get("/api/v1/reports/monthly-summary")
                        .header("Authorization", "Bearer " + token)
                        .param("month", YearMonth.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(0))
                .andExpect(jsonPath("$.expense").value(0))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    void getMonthlyCategorySummary_whenMultipleCategoriesAndTypesExist_thenOnlyMatchingTypeIsGrouped() throws Exception {
        String token = registerAndGetToken("cat-robust@test.com");

        Long food = createCategory(token, "Food", Type.EXPENSE);
        Long transport = createCategory(token, "Transport", Type.EXPENSE);
        Long salary = createCategory(token, "Salary", Type.INCOME);

        createTransaction(token, food, 100, Type.EXPENSE);
        createTransaction(token, transport, 50, Type.EXPENSE);

        createTransaction(token, salary, 1000, Type.INCOME);

        mockMvc.perform(get("/api/v1/reports/monthly-category-summary")
                        .header("Authorization", "Bearer " + token)
                        .param("month", YearMonth.now().toString())
                        .param("type", "EXPENSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].categoryName",
                        containsInAnyOrder("Food", "Transport")));
    }

    @Test
    void getMonthlyCategorySummary_whenWrongTypeProvided_thenReturns400() throws Exception {
        String token = registerAndGetToken("cat-summary-bad@test.com");

        mockMvc.perform(get("/api/v1/reports/monthly-category-summary")
                        .header("Authorization", "Bearer " + token)
                        .param("month", YearMonth.now().toString())
                        .param("type", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPeriodSummary_whenTransactionsInsideAndOutsidePeriodExist_thenOnlyInsidePeriodAreCounted() throws Exception {
        String token = registerAndGetToken("period-robust@test.com");

        Long income = createCategory(token, "Salary", Type.INCOME);
        Long expense = createCategory(token, "Food", Type.EXPENSE);

        LocalDate insideDate = LocalDate.now().minusDays(1);
        LocalDate outsideDate = LocalDate.now().minusDays(10);

        createTransaction(token, income, 500, Type.INCOME, insideDate);
        createTransaction(token, expense, 100, Type.EXPENSE, insideDate);

        createTransaction(token, expense, 999, Type.EXPENSE, outsideDate);

        mockMvc.perform(get("/api/v1/reports/period-summary")
                        .header("Authorization", "Bearer " + token)
                        .param("startDate", LocalDate.now().minusDays(2).toString())
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(500))
                .andExpect(jsonPath("$.expense").value(100))
                .andExpect(jsonPath("$.balance").value(400));
    }


    @Test
    void getPeriodSummary_whenStartDateAfterEndDate_thenReturns400() throws Exception {
        String token = registerAndGetToken("period-bad@test.com");

        mockMvc.perform(get("/api/v1/reports/period-summary")
                        .header("Authorization", "Bearer " + token)
                        .param("startDate", LocalDate.now().toString())
                        .param("endDate", LocalDate.now().minusDays(1).toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMonthlyTrend_whenTransactionsExistInMultipleMonths_thenEachMonthHasCorrectTotal() throws Exception {
        String token = registerAndGetToken("trend-robust@test.com");

        Long food = createCategory(token, "Food", Type.EXPENSE);

        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);

        createTransaction(
                token,
                food,
                50,
                Type.EXPENSE,
                previousMonth.atDay(5)
        );

        createTransaction(
                token,
                food,
                100,
                Type.EXPENSE,
                currentMonth.atDay(5)
        );

        mockMvc.perform(get("/api/v1/reports/monthly-trend")
                        .header("Authorization", "Bearer " + token)
                        .param("from", previousMonth.toString())
                        .param("to", currentMonth.toString())
                        .param("type", "EXPENSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].month").value(previousMonth.toString()))
                .andExpect(jsonPath("$[0].total").value(50))
                .andExpect(jsonPath("$[1].month").value(currentMonth.toString()))
                .andExpect(jsonPath("$[1].total").value(100));
    }

    @Test
    void getMonthlyTrend_whenFromIsAfterTo_thenReturns400() throws Exception {
        String token = registerAndGetToken("trend-bad@test.com");

        mockMvc.perform(get("/api/v1/reports/monthly-trend")
                        .header("Authorization", "Bearer " + token)
                        .param("from", YearMonth.now().toString())
                        .param("to", YearMonth.now().minusMonths(1).toString())
                        .param("type", "EXPENSE"))
                .andExpect(status().isBadRequest());
    }
}

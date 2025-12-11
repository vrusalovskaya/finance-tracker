package com.example.finance_tracker.repositories;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.entities.TransactionEntity;
import com.example.finance_tracker.projections.CategorySummaryProjection;
import com.example.finance_tracker.projections.MonthlyTrendProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<TransactionEntity, Long> {
    @Query("""
                SELECT COALESCE(SUM(t.amount), 0)
                FROM TransactionEntity t
                WHERE t.userEntity.id = :userId
                  AND t.type = :type
                  AND t.date BETWEEN :from AND :to
            """)
    BigDecimal sumByTypeAndDateRange(Long userId, Type type, LocalDate from, LocalDate to);

    @Query("""
                SELECT new com.example.finance_tracker.projections.CategorySummaryProjection(
                    c.id,
                    c.name,
                    COALESCE(SUM(t.amount), 0)
                )
                FROM TransactionEntity t
                JOIN t.categoryEntity c
                WHERE t.userEntity.id = :userId
                  AND t.type = :type
                  AND t.date BETWEEN :from AND :to
                GROUP BY c.id, c.name
            """)
    List<CategorySummaryProjection> monthlySummaryByCategory(Long userId, Type type, LocalDate from, LocalDate to);

    @Query("""
                SELECT new com.example.finance_tracker.projections.MonthlyTrendProjection(
                    EXTRACT(YEAR FROM t.date),
                    EXTRACT(MONTH FROM t.date),
                    COALESCE(SUM(t.amount), 0)
                )
                FROM TransactionEntity t
                WHERE t.userEntity.id = :userId
                  AND t.type = :type
                  AND t.date >= :from
                  AND t.date <= :to
                GROUP BY EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date)
                ORDER BY EXTRACT(YEAR FROM t.date), EXTRACT(MONTH FROM t.date)
            """)
    List<MonthlyTrendProjection> getMonthlyAggregates(
            Long userId,
            Type type,
            LocalDate from,
            LocalDate to
    );
}

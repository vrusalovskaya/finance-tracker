package com.example.finance_tracker.repositories;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.entities.TransactionEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@NullMarked
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    @EntityGraph(attributePaths = {"categoryEntity", "userEntity"})
    List<TransactionEntity> findByUserEntityId(Long userId);

    @EntityGraph(attributePaths = {"categoryEntity", "userEntity"})
    List<TransactionEntity> findByUserEntityIdAndType(Long userId, Type type);

    @EntityGraph(attributePaths = {"categoryEntity", "userEntity"})
    List<TransactionEntity> findByUserEntityIdAndCategoryEntityId(Long userId, Long categoryId);

    @Query("""
                SELECT t FROM TransactionEntity t
                WHERE t.userEntity.id = :userId
                  AND (:from IS NULL OR t.date >= :from)
                  AND (:to IS NULL OR t.date <= :to)
            """)
    @EntityGraph(attributePaths = {"categoryEntity", "userEntity"})
    List<TransactionEntity> findByUserAndDateRange(@Param("userId") Long userId,
                                                   @Param("from") LocalDate from,
                                                   @Param("to") LocalDate to);

}

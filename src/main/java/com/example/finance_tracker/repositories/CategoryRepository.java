package com.example.finance_tracker.repositories;

import com.example.finance_tracker.entities.CategoryEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@NullMarked
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    @EntityGraph(attributePaths = "userEntity")
    List<CategoryEntity> findByUserEntityId(Long userId);
}

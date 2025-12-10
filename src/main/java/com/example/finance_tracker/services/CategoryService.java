package com.example.finance_tracker.services;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.models.Category;

import java.util.List;

public interface CategoryService {
    Category create(Category category);

    Category update(Category category);

    void delete(Long categoryId, Long userId);

    List<Category> getByUser(Long userId);
}

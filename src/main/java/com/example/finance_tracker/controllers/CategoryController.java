package com.example.finance_tracker.controllers;

import com.example.finance_tracker.dtos.CategoryRequest;
import com.example.finance_tracker.dtos.CategoryResponse;
import com.example.finance_tracker.mappers.CategoryMapper;
import com.example.finance_tracker.models.Category;
import com.example.finance_tracker.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest categoryRequest) {
        Category category = categoryMapper.toModel(categoryRequest);
        Category createdCategory = categoryService.create(category);

        CategoryResponse categoryResponse = categoryMapper.toResponse(createdCategory);

        URI location = URI.create(String.format("/api/v1/categories/%d", categoryResponse.id()));
        return ResponseEntity.created(location).body(categoryResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        Category category = categoryMapper.toModel(categoryRequest);
        category.setId(id);
        Category updatedCategory = categoryService.update(category);

        CategoryResponse categoryResponse = categoryMapper.toResponse(updatedCategory);
        return ResponseEntity.ok().body(categoryResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @RequestParam Long userId) {
        categoryService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<CategoryResponse> getByUser(@RequestParam Long userId) {
        return categoryService.getByUser(userId).stream().map(categoryMapper::toResponse).toList();
    }
}

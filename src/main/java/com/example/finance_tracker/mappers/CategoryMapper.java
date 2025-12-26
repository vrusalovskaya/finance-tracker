package com.example.finance_tracker.mappers;

import com.example.finance_tracker.dtos.CategoryRequest;
import com.example.finance_tracker.dtos.CategoryResponse;
import com.example.finance_tracker.entities.CategoryEntity;
import com.example.finance_tracker.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "userEntity.id", target = "userId")
    Category toModel(CategoryEntity entity);

    Category toModel(CategoryRequest categoryRequest);

    CategoryResponse toResponse(Category category);
}

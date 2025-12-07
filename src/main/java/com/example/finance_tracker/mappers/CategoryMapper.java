package com.example.finance_tracker.mappers;

import com.example.finance_tracker.entities.CategoryEntity;
import com.example.finance_tracker.models.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "userEntity.id", target = "userId")
    Category toModel(CategoryEntity entity);

    @Mapping(target = "userEntity", ignore = true)
    @Mapping(target = "transactionEntities", ignore = true)
    CategoryEntity toEntity(Category model);
}

package com.example.finance_tracker.mappers;

import com.example.finance_tracker.entities.TransactionEntity;
import com.example.finance_tracker.models.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "categoryEntity.id", target = "categoryId")
    @Mapping(source = "userEntity.id", target = "userId")
    Transaction toModel(TransactionEntity entity);

    @Mapping(target = "categoryEntity", ignore = true)
    @Mapping(target = "userEntity", ignore = true)
    TransactionEntity toEntity(Transaction model);
}
package com.example.finance_tracker.mappers;

import com.example.finance_tracker.dtos.CreateUserRequest;
import com.example.finance_tracker.dtos.UserResponse;
import com.example.finance_tracker.entities.UserEntity;
import com.example.finance_tracker.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "categoryEntities", ignore = true)
    @Mapping(target = "transactionEntities", ignore = true)
    UserEntity toEntity(User model);

    User toModel(UserEntity entity);

    User toModel(CreateUserRequest createUserRequest);
    UserResponse toResponse(User user);
}

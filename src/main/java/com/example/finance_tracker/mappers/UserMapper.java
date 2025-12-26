package com.example.finance_tracker.mappers;

import com.example.finance_tracker.entities.UserEntity;
import com.example.finance_tracker.models.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toModel(UserEntity entity);
}

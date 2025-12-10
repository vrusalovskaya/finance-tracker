package com.example.finance_tracker.repositories;

import com.example.finance_tracker.entities.UserEntity;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

@NullMarked
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}

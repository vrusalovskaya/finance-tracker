package com.example.finance_tracker.repositories;

import com.example.finance_tracker.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}

package com.example.finance_tracker.services;

import com.example.finance_tracker.models.User;

public interface UserService {
    User create(String userName, String email, String rawPassword);

    User get(Long id);
}

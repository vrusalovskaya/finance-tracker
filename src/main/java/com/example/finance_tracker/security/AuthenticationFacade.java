package com.example.finance_tracker.security;

public interface AuthenticationFacade {
    String login(String email, String password);
    String register(String userName, String email, String rawPassword);
}

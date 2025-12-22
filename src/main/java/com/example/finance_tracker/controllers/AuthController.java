package com.example.finance_tracker.controllers;

import com.example.finance_tracker.dtos.CreateUserRequest;
import com.example.finance_tracker.security.AuthenticationFacade;
import com.example.finance_tracker.dtos.LoginRequest;
import com.example.finance_tracker.dtos.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationFacade auth;

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return new TokenResponse(
                auth.login(request.email(), request.password())
        );
    }

    @PostMapping("/register")
    public TokenResponse register(@Valid @RequestBody CreateUserRequest request) {
        return new TokenResponse(
                auth.register(request.userName(), request.email(), request.password())
        );
    }
}


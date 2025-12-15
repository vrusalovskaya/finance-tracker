package com.example.finance_tracker.controllers;

import com.example.finance_tracker.dtos.CreateUserRequest;
import com.example.finance_tracker.dtos.UserResponse;
import com.example.finance_tracker.mappers.UserMapper;
import com.example.finance_tracker.models.User;
import com.example.finance_tracker.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest createUserRequest) {
        User user = userService.create(
                createUserRequest.userName(),
                createUserRequest.email(),
                createUserRequest.password());

        UserResponse userResponse = userMapper.toResponse(user);

        URI location = URI.create(String.format("/api/v1/users/%d", userResponse.id()));
        return ResponseEntity.created(location).body(userResponse);
    }

    @GetMapping("/{id}")
    public UserResponse get(@PathVariable Long id) {
        return userMapper.toResponse(userService.get(id));
    }
}

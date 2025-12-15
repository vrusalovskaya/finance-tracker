package com.example.finance_tracker.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Username is mandatory")
        @Size(max = 50, message = "Username should be less than 50 characters")
        String userName,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Email format is invalid")
        @Size(max = 50, message = "Email should be less than 50 characters")
        String email,

        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, max = 50, message = "Password should be between 8 and 50 characters")
        String password
) {
}

package com.example.finance_tracker.dtos;

import com.example.finance_tracker.common.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Category name is mandatory")
        @Size(max = 50, message = "Category name should be less than 50 characters")
        String name,

        @NotNull(message = "Category type is mandatory")
        Type type,

        @Size(max = 255, message = "Description length should not exceed 255 characters")
        String description
) {
}

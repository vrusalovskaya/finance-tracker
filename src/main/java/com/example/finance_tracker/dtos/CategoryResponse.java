package com.example.finance_tracker.dtos;

import com.example.finance_tracker.common.Type;

public record CategoryResponse(
        Long id,
        String name,
        Type type,
        String description,
        Long userId
) {
}

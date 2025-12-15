package com.example.finance_tracker.common;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Type {
    INCOME,
    EXPENSE;

    @JsonCreator
    public static Type from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Type must not be null or blank. Allowed values: INCOME, EXPENSE"
            );
        }

        try {
            return Type.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid type: '" + value + "'. Allowed values: INCOME, EXPENSE"
            );
        }
    }
}

package com.example.finance_tracker.models;

import com.example.finance_tracker.common.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private Long id;
    private Type type;
    private BigDecimal amount;
    private LocalDate date;
    private String description;

    private Long categoryId;
    private Long userId;
}

package com.example.finance_tracker.models;

import com.example.finance_tracker.common.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TransactionExportRow {

    private LocalDate date;
    private String categoryName;
    private Type type;
    private BigDecimal amount;
}

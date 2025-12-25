package com.example.finance_tracker.services.export;

import com.example.finance_tracker.common.Type;
import com.example.finance_tracker.models.TransactionExportRow;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransactionTotals {
    private BigDecimal income = BigDecimal.ZERO;
    private BigDecimal expense = BigDecimal.ZERO;

    public void add(TransactionExportRow t) {
        if (t.getType() == Type.INCOME) {
            income = income.add(t.getAmount());
        } else {
            expense = expense.add(t.getAmount());
        }
    }

    public BigDecimal getBalance() {
        return income.subtract(expense);
    }
}

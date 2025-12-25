package com.example.finance_tracker.services.export;

import com.example.finance_tracker.models.TransactionExportRow;

import java.util.List;

public interface ExportStrategy {
    ExportFormat getFormat();

    ExportFile export(List<TransactionExportRow> transactionRows, TransactionTotals totals);
}

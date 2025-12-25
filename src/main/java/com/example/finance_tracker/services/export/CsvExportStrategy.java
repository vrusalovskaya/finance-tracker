package com.example.finance_tracker.services.export;

import com.example.finance_tracker.models.TransactionExportRow;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExportStrategy implements ExportStrategy {

    @Override
    public ExportFormat getFormat() {
        return ExportFormat.CSV;
    }

    @Override
    public ExportFile export(
            List<TransactionExportRow> transactionRows,
            TransactionTotals totals
    ) {
        StringBuilder csv = new StringBuilder();
        csv.append("Date,Category,Type,Amount\n");

        for (TransactionExportRow t : transactionRows) {
            csv.append(t.getDate()).append(",");
            csv.append(escape(t.getCategoryName())).append(",");
            csv.append(t.getType()).append(",");
            csv.append(t.getAmount()).append("\n");
        }

        csv.append("\n");
        csv.append("Income,,," + totals.getIncome()).append("\n");
        csv.append("Expense,,," + totals.getExpense()).append("\n");
        csv.append("Balance,,," + totals.getBalance()).append("\n");

        return new ExportFile(
                csv.toString().getBytes(StandardCharsets.UTF_8),
                "transactions.csv",
                MediaType.parseMediaType("text/csv")
        );
    }

    private String escape(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}


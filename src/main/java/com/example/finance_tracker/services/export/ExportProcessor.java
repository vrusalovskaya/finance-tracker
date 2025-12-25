package com.example.finance_tracker.services.export;

import com.example.finance_tracker.models.TransactionExportRow;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ExportProcessor {

    private final Map<ExportFormat, ExportStrategy> strategies;

    public ExportProcessor(List<ExportStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        ExportStrategy::getFormat,
                        Function.identity()
                ));
    }

    public ExportFile export(
            ExportFormat format,
            List<TransactionExportRow> transactionRows
    ) {
        TransactionTotals totals = new TransactionTotals();
        transactionRows.forEach(totals::add);

        ExportStrategy strategy = strategies.get(format);

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported format: " + format);
        }

        return strategy.export(transactionRows, totals);
    }
}


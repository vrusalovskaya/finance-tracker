package com.example.finance_tracker.services.export;

import com.example.finance_tracker.entities.TransactionEntity;
import com.example.finance_tracker.models.TransactionExportRow;
import com.example.finance_tracker.repositories.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class ExportService {

    private final TransactionRepository transactionRepository;
    private final ExportProcessor exportProcessor;

    public ExportFile export(
            Long userId,
            ExportFormat format,
            LocalDate start,
            LocalDate end
    ) {
        List<TransactionEntity> transactions = transactionRepository.findByUserAndDateRange(userId, start, end);

        List<TransactionExportRow> transactionRows = transactions.stream().map(
                        t -> new TransactionExportRow(
                                t.getDate(),
                                t.getCategoryEntity().getName(),
                                t.getType(),
                                t.getAmount()))
                .toList();

        return exportProcessor.export(format, transactionRows);
    }
}

package com.example.finance_tracker.services.export;

import com.example.finance_tracker.exceptions.ExportException;
import com.example.finance_tracker.models.TransactionExportRow;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class PdfExportStrategy implements ExportStrategy {

    @Override
    public ExportFormat getFormat() {
        return ExportFormat.PDF;
    }

    @Override
    public ExportFile export(
            List<TransactionExportRow> transactionRows,
            TransactionTotals totals
    ) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (Document document = new Document()) {
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Transactions Report"));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.addCell("Date");
            table.addCell("Category");
            table.addCell("Type");
            table.addCell("Amount");

            for (TransactionExportRow r : transactionRows) {
                table.addCell(r.getDate().toString());
                table.addCell(r.getCategoryName());
                table.addCell(r.getType().name());
                table.addCell(r.getAmount().toString());
            }

            document.add(table);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Income: " + totals.getIncome()));
            document.add(new Paragraph("Expense: " + totals.getExpense()));
            document.add(new Paragraph("Balance: " + totals.getBalance()));

        } catch (DocumentException e) {
            throw new ExportException("Failed to generate PDF export", e);
        }

        return new ExportFile(
                out.toByteArray(),
                "transactions.pdf",
                MediaType.APPLICATION_PDF
        );
    }
}

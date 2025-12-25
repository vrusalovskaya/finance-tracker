package com.example.finance_tracker.controllers;

import com.example.finance_tracker.security.CurrentUserProvider;
import com.example.finance_tracker.services.export.ExportFile;
import com.example.finance_tracker.services.export.ExportFormat;
import com.example.finance_tracker.services.export.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;
    private final CurrentUserProvider currentUser;

    @GetMapping
    public ResponseEntity<byte[]> export(
            @RequestParam ExportFormat format,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        Long userId = currentUser.getCurrentUserId();

        ExportFile file = exportService.export(
                userId,
                format,
                startDate,
                endDate
        );

        return ResponseEntity.ok()
                .contentType(file.contentType())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + file.filename()
                )
                .body(file.content());
    }
}


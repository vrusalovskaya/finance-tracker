package com.example.finance_tracker.services.export;

import org.springframework.http.MediaType;


public record ExportFile(byte[] content, String filename, MediaType contentType) {
}

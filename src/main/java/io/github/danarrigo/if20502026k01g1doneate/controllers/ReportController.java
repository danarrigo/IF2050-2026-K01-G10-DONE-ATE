package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.services.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/download/{donatorId}")
    public ResponseEntity<byte[]> downloadReport(@PathVariable UUID donatorId) {
        try {
            byte[] pdfBytes = reportService.generateReport(donatorId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Laporan_Donasi_" + donatorId + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (RuntimeException e) {
            // Check if it's a "not found" error
            if (e.getMessage() != null && e.getMessage().contains("tidak ditemukan")) {
                return ResponseEntity.notFound().build();
            }
            // For other errors, return 500
            return ResponseEntity.internalServerError().build();
        }
    }
}
package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.services.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/download/{donatorId}")
    public ResponseEntity<String> downloadReport(@PathVariable UUID donatorId) {
        try {
            File pdfFile = reportService.generateReport(donatorId);
            return ResponseEntity.ok("Laporan berhasil dibuat di: " + pdfFile.getAbsolutePath());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Gagal: " + e.getMessage());
        }
    }
}
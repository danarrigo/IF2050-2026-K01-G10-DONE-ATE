package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Report;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.repositories.ReportRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonatorRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.io.File;

@RestController
@RequestMapping("/api/reports") // Endpoint standar untuk Spring Boot
public class ReportController {

    private final ReportRepository reportRepository;
    private final DonationRepository donationRepository;
    private final DonatorRepository donatorRepository;
    

    // Ini adalah jawaban dari pertanyaanmu sebelumnya!
    // Spring Boot akan otomatis "mengisi" konstruktor ini dengan repository yang benar.
    public ReportController(ReportRepository reportRepository, DonationRepository donationRepository, DonatorRepository donatorRepository) {
        this.reportRepository = reportRepository;
        this.donationRepository = donationRepository;
        this.donatorRepository = donatorRepository;
    }

    @GetMapping("/download/{donatorId}")
    public void downloadReport(@PathVariable String donatorId) {
        generateReport(donatorId);
    }

public void generateReport(String donatorId) {
        List<Donation> allDonations = donationRepository.findAll();

        List<Donation> donationHistory = allDonations.stream()
                .filter(d -> d.getDonator() != null && d.getDonator().getDonatorId().equals(donatorId))
                .toList();

        Integer totalRescued = aggregateData(donationHistory);

        Donator donator = donatorRepository.findById(donatorId).orElse(null);

        if (donator == null) {
            System.out.println("Gagal membuat laporan: Data donatur tidak ditemukan.");
            return; 
        }

        Report newReport = createReport(donator, totalRescued);

        File pdfFile = generatePDF(newReport);
        System.out.println("Laporan berhasil dibuat: " + pdfFile.getAbsolutePath());
    }

    public Integer aggregateData(List<Donation> donationList) {
        int total = 0;
        
        if (donationList != null) {
            for (Donation donation : donationList) {
                if ("Selesai".equals(donation.getStatus()) || donation.isTaken()) {
                    total += 1;
                }
            }
        }
        return total;
    }

    public Report createReport(Donator donator, Integer totalRescued) {
        Report newReport = new Report(totalRescued, LocalDate.now(), donator);
        reportRepository.save(newReport);
        return newReport;
    }

    private File generatePDF(Report report) {
        return new File("Laporan_Donasi.pdf");
    }
}
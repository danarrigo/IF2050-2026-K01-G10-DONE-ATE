package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Report;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.repositories.ReportRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.io.File;
import java.util.UUID;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final DonationRepository donationRepository;
    private final DonatorRepository donatorRepository;

    public ReportService(ReportRepository reportRepository, DonationRepository donationRepository, DonatorRepository donatorRepository) {
        this.reportRepository = reportRepository;
        this.donationRepository = donationRepository;
        this.donatorRepository = donatorRepository;
    }

    public File generateReport(UUID donatorId) {
        Donator donator = donatorRepository.findById(donatorId)
                .orElseThrow(() -> new RuntimeException("Data donatur tidak ditemukan."));

        // Menggunakan query langsung ke database berbasis UUID
        List<Donation> donationHistory = donationRepository.findByDonator_DonatorId(donatorId);

        Integer totalRescued = aggregateData(donationHistory);
        Report newReport = createReport(donator, totalRescued);

        return generatePDF(newReport);
    }

    private Integer aggregateData(List<Donation> donationList) {
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

    private Report createReport(Donator donator, Integer totalRescued) {
        Report newReport = new Report(totalRescued, LocalDate.now(), donator);
        return reportRepository.save(newReport);
    }

    private File generatePDF(Report report) {
        // TODO: Implementasi PDF generator di iterasi berikutnya
        return new File("Laporan_Donasi.pdf");
    }
}
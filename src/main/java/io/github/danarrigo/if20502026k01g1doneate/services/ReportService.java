package io.github.danarrigo.if20502026k01g1doneate.services;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Report;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.repositories.ReportRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final DonationRepository donationRepository;
    private final DonatorRepository donatorRepository;

    public ReportService(ReportRepository reportRepository, DonationRepository donationRepository, DonatorRepository donatorRepository) {
        this.reportRepository = reportRepository;
        this.donationRepository = donationRepository;
        this.donatorRepository = donatorRepository;
    }

    public byte[] generateReport(UUID donatorId) {
        // Fetch data in read-only transaction
        Donator donator = fetchDonator(donatorId);
        List<Donation> history = fetchDonationHistory(donatorId);
        
        Integer totalRescued = aggregateData(history); 

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // 3. Tulis isi PDF-nya (Tinggal tambah Paragraph)
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("LAPORAN DONASI DONE-ATE", titleFont);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            document.add(new Paragraph("ID Donatur: " + donator.getDonatorId().toString()));
            document.add(new Paragraph("Tanggal Cetak: " + LocalDate.now().toString()));
            document.add(new Paragraph("Total Paket Terselamatkan: " + totalRescued));
            document.add(new Paragraph("\n")); // Enter kosong
            
            document.add(new Paragraph("--- Rincian Riwayat ---", new Font(Font.HELVETICA, 12, Font.BOLD)));
            
            // Looping data riwayat
            for(Donation don : history) {
                String foodName = don.getDish() != null ? don.getDish().getName() : "Makanan";
                document.add(new Paragraph("- " + foodName + " | Status: " + don.getStatus()));
            }

        } catch (Exception e) {
            throw new RuntimeException("Gagal membuat PDF", e);
        } finally {
            document.close();
        }
        
        // Save report in separate transaction
        saveReport(totalRescued, LocalDate.now(), donator);

        // 4. Ubah dokumen jadi aliran byte untuk dikirim ke UI
        return outputStream.toByteArray(); 
    }

    @Transactional(readOnly = true)
    protected Donator fetchDonator(UUID donatorId) {
        return donatorRepository.findByDonatorId(donatorId)
                .orElseThrow(() -> new RuntimeException("Donatur tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    protected List<Donation> fetchDonationHistory(UUID donatorId) {
        return donationRepository.findByDonator_DonatorId(donatorId);
    }

    @Transactional
    protected void saveReport(Integer totalRescued, LocalDate date, Donator donator) {
        Report newReport = new Report(totalRescued, date, donator);
        reportRepository.save(newReport);
    }

    private Integer aggregateData(List<Donation> donationList) {
        int total = 0;
        if (donationList != null) {
            for (Donation donation : donationList) {
                if ("Selesai".equals(donation.getStatus())) {
                    total += 1;
                }
            }
        }
        return total;
    }

}

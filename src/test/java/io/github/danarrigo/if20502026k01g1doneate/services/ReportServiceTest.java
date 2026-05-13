package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Report;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonatorRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private DonatorRepository donatorRepository;

    @InjectMocks
    private ReportService reportService;

    private Donator donator;
    private UUID donatorId;
    private Donation donationSelesai;
    private Donation donationPending;

    @BeforeEach
    void setUp() {
        donatorId = UUID.randomUUID();
        donator = new Donator();
        donator.setDonatorId(donatorId);
        donator.setUsername("donator1");

        Dish dish = new Dish("Nasi Goreng", "path/img");

        donationSelesai = new Donation(dish, LocalDateTime.now(), LocalDateTime.now().minusHours(1), "Selesai", donator);
        donationPending = new Donation(dish, LocalDateTime.now(), LocalDateTime.now().minusHours(1), "QC Pending", donator);
    }

    @Test
    void testGenerateReport_Success() {
        List<Donation> history = Arrays.asList(donationSelesai, donationPending);

        when(donatorRepository.findByDonatorId(donatorId)).thenReturn(Optional.of(donator));
        when(donationRepository.findByDonator_DonatorId(donatorId)).thenReturn(history);
        when(reportRepository.save(any(Report.class))).thenAnswer(i -> i.getArguments()[0]);

        byte[] reportPdf = reportService.generateReport(donatorId);

        assertNotNull(reportPdf);
        assertTrue(reportPdf.length > 0);

        // Verify totalRescued logic (only "Selesai" counts)
        verify(reportRepository, times(1)).save(argThat(report -> 
            report.getTotalRescued() == 1 && report.getDonator().equals(donator)
        ));
    }

    @Test
    void testGenerateReport_DonatorNotFound() {
        when(donatorRepository.findByDonatorId(donatorId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reportService.generateReport(donatorId));
        verify(reportRepository, never()).save(any());
    }

    @Test
    void testFetchDonationHistory() {
        when(donationRepository.findByDonator_DonatorId(donatorId)).thenReturn(Arrays.asList(donationSelesai));
        
        List<Donation> result = reportService.fetchDonationHistory(donatorId);
        
        assertEquals(1, result.size());
        assertEquals("Selesai", result.get(0).getStatus());
    }
}

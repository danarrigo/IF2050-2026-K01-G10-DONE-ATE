package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.Transaction;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.RecipientRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClaimingServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private RecipientRepository recipientRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ClaimingService claimingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateClaimability_Success() {
        UUID donationId = UUID.randomUUID();
        String recipientUsername = "panti_asuhan_a";
        String donatorUsername = "restoran_b";

        Donator donator = new Donator();
        donator.setUsername(donatorUsername);

        Donation donation = new Donation();
        donation.setDonationId(donationId);
        donation.setTaken(false);
        donation.setDonator(donator);

        Recipient recipient = new Recipient();
        recipient.setUsername(recipientUsername);

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));
        when(recipientRepository.findById(recipientUsername)).thenReturn(Optional.of(recipient));

        String result = claimingService.validateClaimability(recipientUsername, donationId);

        assertEquals("success", result);
        assertTrue(donation.isTaken()); // Pastikan field taken berubah jadi true
        
        verify(donationRepository, times(1)).save(donation);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(notificationService, times(1)).sendNotification(donatorUsername, "Donasi Anda telah diklaim");
    }

    @Test
    void validateClaimability_DonationNotFound() {
        UUID donationId = UUID.randomUUID();
        String recipientUsername = "panti_asuhan_a";

        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        String result = claimingService.validateClaimability(recipientUsername, donationId);

        assertEquals("Gagal: Donasi tidak ditemukan", result);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void validateClaimability_DonationAlreadyTaken() {
        UUID donationId = UUID.randomUUID();
        String recipientUsername = "panti_asuhan_a";

        Donation donation = new Donation();
        donation.setTaken(true);

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));

        String result = claimingService.validateClaimability(recipientUsername, donationId);

        assertEquals("Gagal: Anda tidak memenuhi syarat klaim atau donasi sudah diambil", result);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void validateClaimability_RecipientNotFound() {
        UUID donationId = UUID.randomUUID();
        String recipientUsername = "panti_asuhan_a";

        Donation donation = new Donation();
        donation.setTaken(false);

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));
        when(recipientRepository.findById(recipientUsername)).thenReturn(Optional.empty());

        String result = claimingService.validateClaimability(recipientUsername, donationId);

        assertEquals("Gagal: Recipient tidak ditemukan", result);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}

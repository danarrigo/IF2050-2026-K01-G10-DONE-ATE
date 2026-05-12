package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.Transaction;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.RecipientRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.TransactionRepository;
import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.Duration;

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
    @Test
    void cancelClaim_Success() {
        Integer transactionCode = 123456;
        Transaction transaction = new Transaction();
        transaction.setTransactionCode(transactionCode);
        
        Donator donator = new Donator();
        donator.setUsername("restoran_b");
        transaction.setDonator(donator);

        Dish dish = new Dish();
        dish.setExpiresIn(java.time.Duration.ofHours(3)); // kadaluarsa dalam 3 jam
        
        Donation donation = new Donation();
        donation.setDish(dish);
        // Dimasak 1 jam yang lalu
        donation.setTimeCooked(java.time.LocalDateTime.now().minusHours(1));
        donation.setTaken(true);
        transaction.setDonation(donation);

        // Batas waktu = sekarang - 1 jam + 3 jam = sekarang + 2 jam
        // Batas toleransi = batas waktu - 1 jam = sekarang + 1 jam
        // Waktu sekarang masih sebelum batas toleransi (valid)

        when(transactionRepository.findByTransactionCode(transactionCode)).thenReturn(java.util.Optional.of(transaction));

        String result = claimingService.cancelClaim(transactionCode);

        assertEquals("success", result);
        assertFalse(donation.isTaken());
        verify(transactionRepository, times(1)).delete(transaction);
        verify(donationRepository, times(1)).save(donation);
        verify(notificationService, times(1)).sendNotification("restoran_b", "Klaim donasi dibatalkan");
    }

    @Test
    void cancelClaim_TransactionNotFound() {
        Integer transactionCode = 123456;
        when(transactionRepository.findByTransactionCode(transactionCode)).thenReturn(java.util.Optional.empty());

        String result = claimingService.cancelClaim(transactionCode);

        assertEquals("Gagal: Transaksi tidak ditemukan", result);
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void cancelClaim_TimeLimitExceeded() {
        Integer transactionCode = 123456;
        Transaction transaction = new Transaction();
        transaction.setTransactionCode(transactionCode);

        Dish dish = new Dish();
        dish.setExpiresIn(java.time.Duration.ofHours(2)); 
        
        Donation donation = new Donation();
        donation.setDish(dish);
        // Dimasak 3 jam yang lalu
        donation.setTimeCooked(java.time.LocalDateTime.now().minusHours(3));
        transaction.setDonation(donation);

        // Batas waktu = sekarang - 3 jam + 2 jam = sekarang - 1 jam
        // Pembatalan ditolak karena donasi ini saja sudah kadaluarsa (melewati batas)

        when(transactionRepository.findByTransactionCode(transactionCode)).thenReturn(java.util.Optional.of(transaction));

        String result = claimingService.cancelClaim(transactionCode);

        assertEquals("Gagal: Opsi pembatalan terkunci karena melewati batas waktu toleransi", result);
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }
}

    
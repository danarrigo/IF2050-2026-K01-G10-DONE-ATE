package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.Transaction;
import io.github.danarrigo.if20502026k01g1doneate.enums.NotificationType;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    private DonationService donationService;

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
        assertTrue(donation.isTaken());
        
        verify(donationRepository, times(1)).save(donation);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        
        // Verifikasi disesuaikan dengan parameter lengkap
        verify(notificationService, times(1)).sendNotification(
                eq(donator),
                eq("Klaim Donasi"),
                contains("Donasi Anda telah diklaim oleh penerima"),
                eq(donationId),
                eq(NotificationType.DONASI)
        );
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
        dish.setExpiresIn(java.time.Duration.ofHours(3)); 
        
        Donation donation = new Donation();
        UUID donationId = UUID.randomUUID();
        donation.setDonationId(donationId); // Memastikan UUID terisi untuk mock test
        donation.setDish(dish);
        donation.setTimeCooked(java.time.LocalDateTime.now().minusHours(1));
        donation.setTaken(true);
        transaction.setDonation(donation);

        when(transactionRepository.findByTransactionCode(transactionCode)).thenReturn(java.util.Optional.of(transaction));

        String result = claimingService.cancelClaim(transactionCode);

        assertEquals("success", result);
        assertFalse(donation.isTaken());
        verify(transactionRepository, times(1)).delete(transaction);
        verify(donationRepository, times(1)).save(donation);
        
        // Verifikasi disesuaikan dengan parameter lengkap
        verify(notificationService, times(1)).sendNotification(
                eq(donator),
                eq("Pembatalan Klaim"),
                contains("Klaim donasi dibatalkan"),
                eq(donationId),
                eq(NotificationType.DONASI)
        );
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
        donation.setTimeCooked(java.time.LocalDateTime.now().minusHours(3));
        transaction.setDonation(donation);

        when(transactionRepository.findByTransactionCode(transactionCode)).thenReturn(java.util.Optional.of(transaction));

        String result = claimingService.cancelClaim(transactionCode);

        assertEquals("Gagal: Opsi pembatalan terkunci karena melewati batas waktu toleransi", result);
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void validateTransactionCode_Success() {
        Integer transactionCode = 123456;

        Donator donator = new Donator();
        donator.setUsername("restoran_b");

        UUID donationId = UUID.randomUUID();
        Donation donation = new Donation();
        donation.setDonationId(donationId);

        Transaction transaction = new Transaction();
        transaction.setTransactionCode(transactionCode);
        transaction.setStatus("ACTIVE");
        transaction.setDonator(donator);
        transaction.setDonation(donation);

        when(transactionRepository.findByTransactionCode(transactionCode)).thenReturn(java.util.Optional.of(transaction));

        String result = claimingService.validateTransactionCode(transactionCode);

        assertEquals("success", result);
        assertEquals("COMPLETED", transaction.getStatus());
        verify(transactionRepository, times(1)).save(transaction);
        verify(donationService, times(1)).removeDonation(donationId);
        verify(notificationService, times(1)).sendNotification(
                donator,
                "Serah Terima Sukses",
                "Serah terima sukses. Makanan telah diterima oleh Penerima.",
                donationId,
                NotificationType.DONASI
        );
    }

    @Test
    void validateTransactionCode_WrongCode() {
        Integer inputCode = 111111;

        when(transactionRepository.findByTransactionCode(inputCode)).thenReturn(java.util.Optional.empty());

        String result = claimingService.validateTransactionCode(inputCode);

        assertEquals("error", result);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(donationService, never()).removeDonation(any(UUID.class));
        verify(notificationService, never()).sendNotification(any(), any(), any(), any(), any());
    }

    @Test
    void validateTransactionCode_TransactionNotFound() {
        Integer transactionCode = 654321;

        when(transactionRepository.findByTransactionCode(transactionCode)).thenReturn(java.util.Optional.empty());

        String result = claimingService.validateTransactionCode(transactionCode);

        assertEquals("error", result);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(donationService, never()).removeDonation(any(UUID.class));
    }

    @Test
    void validateTransactionCode_AlreadyCompleted() {
        Integer transactionCode = 123456;

        Transaction transaction = new Transaction();
        transaction.setTransactionCode(transactionCode);
        transaction.setStatus("COMPLETED");

        when(transactionRepository.findByTransactionCode(transactionCode)).thenReturn(java.util.Optional.of(transaction));

        String result = claimingService.validateTransactionCode(transactionCode);

        assertEquals("error", result);
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(donationService, never()).removeDonation(any(UUID.class));
    }
}
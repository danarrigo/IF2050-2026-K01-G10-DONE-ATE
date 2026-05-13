package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.Transaction;
import io.github.danarrigo.if20502026k01g1doneate.enums.NotificationType;
import io.github.danarrigo.if20502026k01g1doneate.enums.TransactionStatus;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.RecipientRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;

@Service
public class ClaimingService {
    private final DonationRepository donationRepository;
    private final RecipientRepository recipientRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final DonationService donationService; // Ditambahkan untuk memanggil removeDonation

    public ClaimingService(DonationRepository donationRepository, RecipientRepository recipientRepository, TransactionRepository transactionRepository, NotificationService notificationService, DonationService donationService) {
        this.donationRepository = donationRepository;
        this.recipientRepository = recipientRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
        this.donationService = donationService;
    }


    @Transactional
    public String validateTransactionCode(Integer inputCode) {
        // 1. findTransactionByCode
        Transaction transaction = findTransactionByCode(inputCode);
        if (transaction == null) {
            return "error";
        }

        // Only allow verification for ACTIVE transactions
        if (!TransactionStatus.ACTIVE.name().equals(transaction.getStatus())) {
            return "error";
        }

        // 2. getTransactionData
        Integer transactionDataCode = transaction.getTransactionCode();

        // 3. verifyCode
        boolean isValid = verifyCode(inputCode, transactionDataCode);

        if (isValid) {
            // 4. removeClaimability (reuse already-loaded entity to avoid extra DB roundtrip)
            removeClaimability(transaction);

            // 5. removeDonation (Memanggil DonationService yang mewakili DonationController)
            if (transaction.getDonation() != null) {
                donationService.removeDonation(transaction.getDonation().getDonationId());
            }

            // 6 & 7. create Notification & sendNotification
            if (transaction.getDonator() != null) {
                notificationService.sendNotification(
                        transaction.getDonator(),
                        "Serah Terima Sukses",
                        "Serah terima sukses. Makanan telah diterima oleh Penerima.",
                        transaction.getDonation() != null ? transaction.getDonation().getDonationId() : null,
                        NotificationType.DONASI
                );
            }

            return "success";
        } else {
            return "error";
        }
    }

    private Transaction findTransactionByCode(Integer inputCode) {
        return transactionRepository.findByTransactionCode(inputCode).orElse(null);
    }

    private boolean verifyCode(Integer inputCode, Integer transactionDataCode) {
        return inputCode.equals(transactionDataCode);
    }

    private void removeClaimability(Transaction transaction) {
        transaction.setStatus("COMPLETED"); // Menandakan tidak bisa diklaim lagi
        transactionRepository.save(transaction);
    }

    @Transactional
    public String validateClaimability(String recipientUsername, UUID donationId) {
        Donation donation = donationRepository.findById(donationId).orElse(null);
        if (donation == null) {
            return "Gagal: Donasi tidak ditemukan";
        }

        if (donation.isTaken()) {
            return "Gagal: Anda tidak memenuhi syarat klaim atau donasi sudah diambil";
        }

        Recipient recipient = recipientRepository.findById(recipientUsername).orElse(null);
        if (recipient == null) {
            return "Gagal: Recipient tidak ditemukan";
        }

        // Lock donation
        donation.setTaken(true);
        donationRepository.save(donation);

        // Generate Transaction Code
        Integer transactionCode = generateTransactionCode();

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionCode(transactionCode);
        transaction.setRecipient(recipient);
        transaction.setDonator(donation.getDonator());
        transaction.setDonation(donation);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.ACTIVE.name());
        transactionRepository.save(transaction);

        // Send notification to donator with verification code
        if (donation.getDonator() != null) {
            notificationService.sendNotification(
                    donation.getDonator(),
                    "Klaim Donasi",
                    "Donasi Anda telah diklaim oleh penerima. Kode verifikasi serah terima: " + transactionCode,
                    donation.getDonationId(),
                    NotificationType.DONASI
            );
        }

        return "success";
    }

    private Integer generateTransactionCode() {
        return 100000 + new Random().nextInt(900000); // Generates a 6 digit code
    }

    @Transactional
    public String cancelClaim(Integer transactionCode) {
        Transaction transaction = transactionRepository.findByTransactionCode(transactionCode).orElse(null);
        if (transaction == null) {
            return "Gagal: Transaksi tidak ditemukan";
        }

        Donation donation = transaction.getDonation();
        if (donation == null || donation.getDish() == null || donation.getTimeCooked() == null || donation.getDish().getExpiresIn() == null) {
            return "Gagal: Data donasi tidak lengkap untuk menghitung batas waktu";
        }

        LocalDateTime batasWaktu = donation.getTimeCooked().plus(donation.getDish().getExpiresIn());
        LocalDateTime currentTime = LocalDateTime.now();

        if (currentTime.isBefore(batasWaktu.minusHours(1)) || currentTime.isEqual(batasWaktu.minusHours(1))) {
            transactionRepository.delete(transaction);
            
            donation.setTaken(false);
            donationRepository.save(donation);
            
            // Mengirim notifikasi pembatalan dengan format baru
            if (transaction.getDonator() != null) {
                notificationService.sendNotification(
                        transaction.getDonator(),
                        "Pembatalan Klaim",
                        "Klaim donasi dibatalkan",
                        donation.getDonationId(),
                        NotificationType.DONASI
                );
            }
            return "success";
        } else {
            return "Gagal: Opsi pembatalan terkunci karena melewati batas waktu toleransi";
        }
    }
}
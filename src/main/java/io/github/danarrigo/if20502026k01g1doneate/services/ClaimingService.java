package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.Transaction;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.RecipientRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Random;
import java.util.UUID;

@Service
public class ClaimingService {
    private final DonationRepository donationRepository;
    private final RecipientRepository recipientRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    public ClaimingService(DonationRepository donationRepository, RecipientRepository recipientRepository, TransactionRepository transactionRepository, NotificationService notificationService) {
        this.donationRepository = donationRepository;
        this.recipientRepository = recipientRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
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
        transaction.setTransactionTime(LocalTime.now());
        transaction.setStatus("ACTIVE");

        transactionRepository.save(transaction);

        // Send notification to donator
        if (donation.getDonator() != null) {
            notificationService.sendNotification(donation.getDonator().getUsername(), "Donasi Anda telah diklaim");
        }

        return "success";
    }

    private Integer generateTransactionCode() {
        return 100000 + new Random().nextInt(900000); // Generates a 6 digit code
    }
}

package io.github.danarrigo.if20502026k01g1doneate.dtos;

import io.github.danarrigo.if20502026k01g1doneate.entities.Transaction;

import java.time.LocalTime;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionId,
        Integer transactionCode,
        String recipientUsername,
        String donatorUsername,
        UUID donationId,
        LocalTime transactionTime,
        String status
) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getTransactionId(),
                transaction.getTransactionCode(),
                transaction.getRecipient() != null ? transaction.getRecipient().getUsername() : null,
                transaction.getDonator() != null ? transaction.getDonator().getUsername() : null,
                transaction.getDonation() != null ? transaction.getDonation().getDonationId() : null,
                transaction.getTransactionTime(),
                transaction.getStatus()
        );
    }
}

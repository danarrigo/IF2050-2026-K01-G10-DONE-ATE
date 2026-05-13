package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.dtos.TransactionCreateRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.TransactionResponse;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.Transaction;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonatorRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.RecipientRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final RecipientRepository recipientRepository;
    private final DonatorRepository donatorRepository;

    public TransactionService(TransactionRepository transactionRepository, RecipientRepository recipientRepository, DonatorRepository donatorRepository) {
        this.transactionRepository = transactionRepository;
        this.recipientRepository = recipientRepository;
        this.donatorRepository = donatorRepository;
    }

    public TransactionResponse createTransaction(TransactionCreateRequest request) {
        Recipient recipient = recipientRepository.findById(request.recipientUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient not found: " + request.recipientUsername()));
        Donator donator = donatorRepository.findById(request.donatorUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donator not found: " + request.donatorUsername()));

        Transaction transaction = new Transaction();
        transaction.setTransactionCode(request.transactionCode());
        transaction.setRecipient(recipient);
        transaction.setDonator(donator);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setStatus("ACTIVE");

        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    public TransactionResponse getTransactionData(UUID id) {
        return TransactionResponse.from(findTransaction(id));
    }

    private Transaction findTransaction(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    public LocalDateTime getTransactionTime(UUID id) {
        return findTransaction(id).getTransactionTime();
    }

    public LocalDateTime getCancellationTimeLimit(UUID id) {
        Transaction transaction = findTransaction(id);
        if (transaction.getTransactionTime() != null) {
            return transaction.getTransactionTime().plusHours(2);
        }
        return null;
    }

    public TransactionResponse getTransactionByDonationId(UUID donationId) {
        return transactionRepository.findByDonation_DonationId(donationId)
                .map(TransactionResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found for donation: " + donationId));
    }
}

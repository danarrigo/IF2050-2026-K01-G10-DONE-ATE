package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.dtos.TransactionCreateRequest;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.Transaction;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonatorRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.RecipientRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
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

    public Transaction createTransaction(TransactionCreateRequest request) {
        Recipient recipient = recipientRepository.findById(request.recipientUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient not found: " + request.recipientUsername()));
        Donator donator = donatorRepository.findById(request.donatorUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donator not found: " + request.donatorUsername()));

        Transaction transaction = new Transaction();
        transaction.setTransactionCode(request.transactionCode());
        transaction.setRecipient(recipient);
        transaction.setDonator(donator);
        transaction.setTransactionTime(LocalTime.now());
        transaction.setStatus("ACTIVE");

        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionData(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
    }

    public LocalTime getTransactionTime(UUID id) {
        Transaction transaction = getTransactionData(id);
        return transaction.getTransactionTime();
    }

    public LocalTime getCancellationTimeLimit(UUID id) {
        Transaction transaction = getTransactionData(id);
        if (transaction.getTransactionTime() != null) {
            return transaction.getTransactionTime().plusHours(2);
        }
        return null;
    }
}

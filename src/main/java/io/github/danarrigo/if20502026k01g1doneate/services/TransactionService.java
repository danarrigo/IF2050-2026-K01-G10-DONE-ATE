package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.dtos.TransactionCreateRequest;
import io.github.danarrigo.if20502026k01g1doneate.entities.Transaction;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonatorRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.RecipientRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

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
        Transaction transaction = new Transaction();
        transaction.setTransactionCode(request.transactionCode());
        transaction.setRecipient(recipientRepository.findById(request.recipientUserName()).orElse(null));
        transaction.setDonator(donatorRepository.findById(request.donatorUserName()).orElse(null));
        transaction.setTransactionTime(LocalTime.now());
        transaction.setStatus("CREATED");
        
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionData(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
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

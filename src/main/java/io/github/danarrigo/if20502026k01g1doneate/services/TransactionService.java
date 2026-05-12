package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.dtos.TransactionCreateRequest;
import io.github.danarrigo.if20502026k01g1doneate.entities.Transaction;
import io.github.danarrigo.if20502026k01g1doneate.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction createTransaction(TransactionCreateRequest request) {
        Transaction transaction = new Transaction();
        transaction.setTransactionCode(request.transactionCode());
        transaction.setRecipientUserName(request.recipientUserName());
        transaction.setDonatorUserName(request.donatorUserName());
        transaction.setTransactionTime(LocalTime.now());
        transaction.setStatus("CREATED");
        
        return transactionRepository.save(transaction);
    }

    public Transaction getTransactionData(Integer id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public LocalTime getTransactionTime(Integer id) {
        Transaction transaction = getTransactionData(id);
        return transaction.getTransactionTime();
    }

    public LocalTime getCancellationTimeLimit(Integer id) {
        Transaction transaction = getTransactionData(id);
        if (transaction.getTransactionTime() != null) {
            return transaction.getTransactionTime().plusHours(2);
        }
        return null;
    }
}

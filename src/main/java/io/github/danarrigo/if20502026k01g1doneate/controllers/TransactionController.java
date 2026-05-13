package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.dtos.TransactionCreateRequest;
import io.github.danarrigo.if20502026k01g1doneate.entities.Transaction;
import io.github.danarrigo.if20502026k01g1doneate.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionCreateRequest request) {
        Transaction createdTransaction = transactionService.createTransaction(request);
        return ResponseEntity.ok(createdTransaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionData(@PathVariable UUID id) {
        Transaction transaction = transactionService.getTransactionData(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/{id}/time")
    public ResponseEntity<LocalTime> getTransactionTime(@PathVariable UUID id) {
        LocalTime transactionTime = transactionService.getTransactionTime(id);
        return ResponseEntity.ok(transactionTime);
    }

    @GetMapping("/{id}/cancellation-limit")
    public ResponseEntity<LocalTime> getCancellationTimeLimit(@PathVariable UUID id) {
        LocalTime limitTime = transactionService.getCancellationTimeLimit(id);
        return ResponseEntity.ok(limitTime);
    }
}

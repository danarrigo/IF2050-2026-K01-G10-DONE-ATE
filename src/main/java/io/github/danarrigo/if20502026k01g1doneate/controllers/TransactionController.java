package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.dtos.TransactionCreateRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.TransactionResponse;
import io.github.danarrigo.if20502026k01g1doneate.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionCreateRequest request) {
        TransactionResponse createdTransaction = transactionService.createTransaction(request);
        return ResponseEntity.ok(createdTransaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionData(@PathVariable UUID id) {
        TransactionResponse transaction = transactionService.getTransactionData(id);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/{id}/time")
    public ResponseEntity<LocalDateTime> getTransactionTime(@PathVariable UUID id) {
        LocalDateTime transactionTime = transactionService.getTransactionTime(id);
        return ResponseEntity.ok(transactionTime);
    }

    @GetMapping("/{id}/cancellation-limit")
    public ResponseEntity<LocalDateTime> getCancellationTimeLimit(@PathVariable UUID id) {
        LocalDateTime limitTime = transactionService.getCancellationTimeLimit(id);
        return ResponseEntity.ok(limitTime);
    }

    @GetMapping("/donation/{donationId}")
    public ResponseEntity<TransactionResponse> getTransactionByDonationId(@PathVariable UUID donationId) {
        TransactionResponse transaction = transactionService.getTransactionByDonationId(donationId);
        return ResponseEntity.ok(transaction);
    }
}

package io.github.danarrigo.if20502026k01g1doneate.dtos;

public record TransactionCreateRequest(
    int transactionCode,
    String recipientUserName,
    String donatorUserName
) {}

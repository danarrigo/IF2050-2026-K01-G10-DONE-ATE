package io.github.danarrigo.if20502026k01g1doneate.entities;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name="transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;
    
    private int transactionCode;
    private String recipientUserName;
    private String donatorUserName;
    private LocalTime transactionTime;
    private String status;

    public Transaction() {
    }

    // Getters and Setters
    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public int getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(int transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getRecipientUserName() {
        return recipientUserName;
    }

    public void setRecipientUserName(String recipientUserName) {
        this.recipientUserName = recipientUserName;
    }

    public String getDonatorUserName() {
        return donatorUserName;
    }

    public void setDonatorUserName(String donatorUserName) {
        this.donatorUserName = donatorUserName;
    }

    public LocalTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
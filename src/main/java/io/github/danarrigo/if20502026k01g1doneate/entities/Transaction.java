package io.github.danarrigo.if20502026k01g1doneate.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID transactionId;

    @Column(nullable = false, unique = true)
    private Integer transactionCode;

    @ManyToOne
    @JoinColumn(name = "recipient_username", referencedColumnName = "username")
    private Recipient recipient;

    @ManyToOne
    @JoinColumn(name = "donator_username", referencedColumnName = "username")
    private Donator donator;

    @ManyToOne
    @JoinColumn(name = "donation_id", referencedColumnName = "donationId")
    private Donation donation;

    private LocalDateTime transactionTime;

    private String status;

    public Transaction() {}

    public Transaction(Integer transactionCode, Recipient recipient, Donator donator, Donation donation, LocalDateTime transactionTime, String status) {
        this.transactionCode = transactionCode;
        this.recipient = recipient;
        this.donator = donator;
        this.donation = donation;
        this.transactionTime = transactionTime;
        this.status = status;
    }

    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

    public Integer getTransactionCode() { return transactionCode; }
    public void setTransactionCode(Integer transactionCode) { this.transactionCode = transactionCode; }

    public Recipient getRecipient() { return recipient; }
    public void setRecipient(Recipient recipient) { this.recipient = recipient; }

    public Donator getDonator() { return donator; }
    public void setDonator(Donator donator) { this.donator = donator; }

    public Donation getDonation() { return donation; }
    public void setDonation(Donation donation) { this.donation = donation; }

    public LocalDateTime getTransactionTime() { return transactionTime; }
    public void setTransactionTime(LocalDateTime transactionTime) { this.transactionTime = transactionTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

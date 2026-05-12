package io.github.danarrigo.if20502026k01g1doneate.dtos;

import java.util.UUID;

public class ClaimRequestDTO {
    private String recipientUsername;
    private UUID donationId;

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public UUID getDonationId() {
        return donationId;
    }

    public void setDonationId(UUID donationId) {
        this.donationId = donationId;
    }
}

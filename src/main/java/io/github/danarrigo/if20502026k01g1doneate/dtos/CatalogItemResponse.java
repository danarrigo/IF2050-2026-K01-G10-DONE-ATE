package io.github.danarrigo.if20502026k01g1doneate.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class CatalogItemResponse {
    private UUID donationId;
    private UUID dishId;
    private String dishName;
    private String imagePath;
    private long expiresInMinutes;
    private LocalDateTime timeAdded;
    private LocalDateTime timeCooked;
    private boolean taken;
    private boolean ongoing;
    private String status;
    private String donatorUsername;
    private String donatorAddress;
    private String donatorPhoneNumber;

    public CatalogItemResponse() {}

    public UUID getDonationId() { return donationId; }
    public void setDonationId(UUID donationId) { this.donationId = donationId; }

    public UUID getDishId() { return dishId; }
    public void setDishId(UUID dishId) { this.dishId = dishId; }

    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public long getExpiresInMinutes() { return expiresInMinutes; }
    public void setExpiresInMinutes(long expiresInMinutes) { this.expiresInMinutes = expiresInMinutes; }

    public LocalDateTime getTimeAdded() { return timeAdded; }
    public void setTimeAdded(LocalDateTime timeAdded) { this.timeAdded = timeAdded; }

    public LocalDateTime getTimeCooked() { return timeCooked; }
    public void setTimeCooked(LocalDateTime timeCooked) { this.timeCooked = timeCooked; }

    public boolean isTaken() { return taken; }
    public void setTaken(boolean taken) { this.taken = taken; }

    public boolean isOngoing() { return ongoing; }
    public void setOngoing(boolean ongoing) { this.ongoing = ongoing; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDonatorUsername() { return donatorUsername; }
    public void setDonatorUsername(String donatorUsername) { this.donatorUsername = donatorUsername; }

    public String getDonatorAddress() {
        return donatorAddress;
    }

    public void setDonatorAddress(String donatorAddress) {
        this.donatorAddress = donatorAddress;
    }

    public String getDonatorPhoneNumber() {
        return donatorPhoneNumber;
    }

    public void setDonatorPhoneNumber(String donatorPhoneNumber) {
        this.donatorPhoneNumber = donatorPhoneNumber;
    }
}

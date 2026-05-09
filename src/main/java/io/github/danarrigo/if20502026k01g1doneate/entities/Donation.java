package io.github.danarrigo.if20502026k01g1doneate.entities;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name="donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID donationId;
    private boolean taken = false;
    @ManyToOne
    private Dish dish;
    private LocalTime timeAdded;
    private LocalTime timeCooked;
    private boolean ongoing = false;
    private String status;
    @ManyToOne
    private Donator donator;
    public Donation() {
    }

    public Donation( Dish dish, LocalTime timeAdded, LocalTime timeCooked,String status, Donator donator) {
        this.dish = dish;
        this.timeAdded = timeAdded;
        this.timeCooked = timeCooked;
        this.status = status;
        this.donator = donator;
    }

    public String fetchDonationRecord(){
        String dishInfo = (dish == null) ? "null" : dish.getName();
        String added = (timeAdded == null) ? "null" : timeAdded.toString();
        String cooked = (timeCooked == null) ? "null" : timeCooked.toString();

        return "donationId:" + donationId
                + ", taken:" + taken
                + ", dish:" + dishInfo
                + ", timeAdded:" + added
                + ", timeCooked:" + cooked
                + ", onGoing:" + ongoing
                + ", status:" + (status == null ? "null" : status);
    }

    public UUID getDonationId() {
        return donationId;
    }

    public void setDonationId(UUID donationID) {
        this.donationId = donationID;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public LocalTime getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(LocalTime timeAdded) {
        this.timeAdded = timeAdded;
    }

    public LocalTime getTimeCooked() {
        return timeCooked;
    }

    public void setTimeCooked(LocalTime timeCooked) {
        this.timeCooked = timeCooked;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public void setOngoing(boolean onGoing) {
        this.ongoing = onGoing;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Donator getDonator() {
        return donator;
    }

    public void setDonator(Donator donator) {
        this.donator = donator;
    }
}


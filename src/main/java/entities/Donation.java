package entities;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name="donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID donationID;
    private boolean taken ;
    @ManyToOne
    private Dish dish;
    private LocalTime timeAdded;
    private LocalTime timeCooked;
    private boolean onGoing;
    private String status;
    @ManyToOne
    private Donator donator;
    public Donation() {
    }

    public Donation( boolean taken, Dish dish, LocalTime timeAdded, LocalTime timeCooked, boolean onGoing, String status, Donator donator) {
        this.taken = taken;
        this.dish = dish;
        this.timeAdded = timeAdded;
        this.timeCooked = timeCooked;
        this.onGoing = onGoing;
        this.status = status;
        this.donator = donator;
    }

    public String fetchDonationRecord(){
        String dishInfo = (dish == null) ? "null" : dish.getName();
        String added = (timeAdded == null) ? "null" : timeAdded.toString();
        String cooked = (timeCooked == null) ? "null" : timeCooked.toString();

        return "donationId:" + donationID
                + ", taken:" + taken
                + ", dish:" + dishInfo
                + ", timeAdded:" + added
                + ", timeCooked:" + cooked
                + ", onGoing:" + onGoing
                + ", status:" + (status == null ? "null" : status);
    }

    public UUID getDonationID() {
        return donationID;
    }

    public void setDonationID(UUID donationID) {
        this.donationID = donationID;
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

    public boolean isOnGoing() {
        return onGoing;
    }

    public void setOnGoing(boolean onGoing) {
        this.onGoing = onGoing;
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


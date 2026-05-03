package entities;

import enums.DonatorType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="donators")
public class Donator extends User{
    @Column(unique = true, nullable = false)
    private UUID donatorId = UUID.randomUUID();
    @Enumerated(EnumType.STRING)
    private DonatorType donatorType;
    @OneToMany(mappedBy = "donator", cascade = CascadeType.ALL)
    private List<Donation> donations = new ArrayList<>();

    public Donator(String username, String password, String address, String phoneNumber, String email, List<String> notificationList, DonatorType donatorType) {
        super(username, password, address, phoneNumber, email, notificationList);
        this.donatorType = donatorType;
    }

    public Donator() {

    }


    public UUID getDonatorId() {
        return donatorId;
    }

    public void setDonatorId(UUID donatorId) {
        this.donatorId = donatorId;
    }

    public DonatorType getDonatorType() {
        return donatorType;
    }

    public void setDonatorType(DonatorType donatorType) {
        this.donatorType = donatorType;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }
}

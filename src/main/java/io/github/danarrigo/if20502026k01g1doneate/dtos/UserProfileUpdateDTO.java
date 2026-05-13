package io.github.danarrigo.if20502026k01g1doneate.dtos;

import io.github.danarrigo.if20502026k01g1doneate.enums.DonatorType;

public class UserProfileUpdateDTO {
    private String email;
    private String phoneNumber;
    private String address;
    private DonatorType donatorType;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DonatorType getDonatorType() {
        return donatorType;
    }

    public void setDonatorType(DonatorType donatorType) {
        this.donatorType = donatorType;
    }
}

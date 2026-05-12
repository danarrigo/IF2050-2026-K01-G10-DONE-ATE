package io.github.danarrigo.if20502026k01g1doneate.dtos;

import io.github.danarrigo.if20502026k01g1doneate.enums.DonatorType;

public class DonatorRegistrationRequest {
    private String username;
    private String password;
    private String address;
    private String phoneNumber;
    private String email;
    private DonatorType donatorType;

    public DonatorRegistrationRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DonatorType getDonatorType() {
        return donatorType;
    }

    public void setDonatorType(DonatorType donatorType) {
        this.donatorType = donatorType;
    }
}

package io.github.danarrigo.if20502026k01g1doneate.dtos;

import io.github.danarrigo.if20502026k01g1doneate.enums.RecipientType;

import java.time.LocalTime;

public class RecipientRegistrationRequest {
    private String username;
    private String password;
    private String address;
    private String phoneNumber;
    private String email;
    private String fullName;
    private LocalTime operationalTimeStart;
    private LocalTime operationalTimeEnd;
    private RecipientType recipientType;

    public RecipientRegistrationRequest() {
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalTime getOperationalTimeStart() {
        return operationalTimeStart;
    }

    public void setOperationalTimeStart(LocalTime operationalTimeStart) {
        this.operationalTimeStart = operationalTimeStart;
    }

    public LocalTime getOperationalTimeEnd() {
        return operationalTimeEnd;
    }

    public void setOperationalTimeEnd(LocalTime operationalTimeEnd) {
        this.operationalTimeEnd = operationalTimeEnd;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }
}

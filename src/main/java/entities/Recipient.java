package entities;

import enums.RecipientType;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="recipients")
public class Recipient extends User {
    @Column(unique = true, nullable = false)
    private UUID recipientID = UUID.randomUUID();
    private String fullName;
    private LocalTime operationalTimeStart;
    private LocalTime operationalTimeEnd;
    @Enumerated(EnumType.STRING)
    private RecipientType recipientType;

    public Recipient(String username, String password, String address, String phoneNumber, String email, List<String> notificationList, UUID recipientID, String fullName, LocalTime operationalTimeStart, LocalTime operationalTimeEnd, RecipientType recipientType) {
        super(username, password, address, phoneNumber, email, notificationList);
        this.recipientID = recipientID;
        this.fullName = fullName;
        this.operationalTimeStart = operationalTimeStart;
        this.operationalTimeEnd = operationalTimeEnd;
        this.recipientType = recipientType;
    }

    public Recipient() {

    }

    public UUID getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(UUID recipientID) {
        this.recipientID = recipientID;
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

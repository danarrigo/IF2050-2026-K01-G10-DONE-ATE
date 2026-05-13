package io.github.danarrigo.if20502026k01g1doneate.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

import io.github.danarrigo.if20502026k01g1doneate.enums.NotificationType;

@Entity
@Table(name="notifications")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID notificationId;

    @Column(name = "title")
    private String title;
    
    private String messageBody;
    
    private LocalDateTime timeStamp;
    
    private boolean isRead = false;

    private UUID relatedDonationId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_username")
    @JsonIgnore
    private User user;

    public Notification() {
    }

    public Notification(String messageBody, LocalDateTime timeStamp, User user) {
        this.messageBody = messageBody;
        this.timeStamp = timeStamp;
        this.user = user;
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getRelatedDonationId() { 
        return relatedDonationId; 
    }
    
    public void setRelatedDonationId(UUID relatedDonationId) { 
        this.relatedDonationId = relatedDonationId; 
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
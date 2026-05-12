package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Notification;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    //CREATE
    public Notification sendNotification(User user, String messageBody) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessageBody(messageBody);
        notification.setTimeStamp(LocalDateTime.now());
        notification.setRead(false);
        
        return notificationRepository.save(notification);
    }

    //READ
    public List<Notification> getUserNotifications(String username) {
        return notificationRepository.findByUser_UsernameOrderByTimeStampDesc(username);
    }

    //UPDATE
    public Notification markAsRead(UUID notificationId) {        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notifikasi tidak ditemukan!"));

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    //DELETE
    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
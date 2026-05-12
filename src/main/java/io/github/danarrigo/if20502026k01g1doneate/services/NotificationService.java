package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Notification;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.enums.NotificationType;
import io.github.danarrigo.if20502026k01g1doneate.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public Notification sendNotification(User user, String title, String messageBody, UUID relatedDonationId, NotificationType type) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessageBody(messageBody);
        notification.setTimeStamp(LocalDateTime.now());
        notification.setRead(false);
        notification.setRelatedDonationId(relatedDonationId);
        notification.setType(type);
        
        return notificationRepository.save(notification);
    }

    public Slice<Notification> getUserNotifications(String username, String filter, Pageable pageable) {
        if ("UNREAD".equalsIgnoreCase(filter)) {
            return notificationRepository.findByUser_UsernameAndIsReadFalseOrderByTimeStampDesc(username, pageable);
        } else if ("DONASI".equalsIgnoreCase(filter)) {
            return notificationRepository.findByUser_UsernameAndTypeOrderByTimeStampDesc(username, NotificationType.DONASI, pageable);
        } else if ("SISTEM".equalsIgnoreCase(filter)) {
            return notificationRepository.findByUser_UsernameAndTypeOrderByTimeStampDesc(username, NotificationType.SISTEM, pageable);
        }
        
        return notificationRepository.findByUser_UsernameOrderByTimeStampDesc(username, pageable);
    }

    public Notification markAsRead(UUID notificationId) {        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notifikasi tidak ditemukan!"));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
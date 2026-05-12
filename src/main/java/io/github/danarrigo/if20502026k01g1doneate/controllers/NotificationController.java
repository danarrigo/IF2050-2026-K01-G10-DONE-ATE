package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.entities.Notification;
import io.github.danarrigo.if20502026k01g1doneate.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    //GET
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String username) {
        List<Notification> notifications = notificationService.getUserNotifications(username);
        return ResponseEntity.ok(notifications);
    }

    //PUT
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markNotificationAsRead(@PathVariable UUID id) {
        Notification updatedNotification = notificationService.markAsRead(id);
        return ResponseEntity.ok(updatedNotification);
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
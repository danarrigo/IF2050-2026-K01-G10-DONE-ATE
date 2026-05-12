package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class NotificationService {
    private final UserRepository userRepository;

    public NotificationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void sendNotification(String username, String message) {
        userRepository.findById(username).ifPresent(user -> {
            if (user.getNotificationList() == null) {
                user.setNotificationList(new ArrayList<>());
            }
            user.getNotificationList().add(message);
            userRepository.save(user);
        });
    }
}

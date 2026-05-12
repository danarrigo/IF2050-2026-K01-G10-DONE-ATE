package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Notification;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.enums.NotificationType;
import io.github.danarrigo.if20502026k01g1doneate.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User mockUser;
    private Notification mockNotification;
    private UUID notificationId;
    private UUID donationId;

    @BeforeEach
    void setUp() {
        mockUser = new User() {};
        mockUser.setUsername("Diddybluds");
        
        notificationId = UUID.randomUUID();
        donationId = UUID.randomUUID();
        
        // UPDATE: Penyesuaian pembuatan mock object karena ada tambahan atribut Title
        mockNotification = new Notification();
        mockNotification.setNotificationId(notificationId);
        mockNotification.setTitle("Donasi Diterima"); // Tambahan title
        mockNotification.setMessageBody("Donasi Anda telah diklaim!");
        mockNotification.setTimeStamp(LocalDateTime.now());
        mockNotification.setUser(mockUser);
        mockNotification.setRelatedDonationId(donationId);
        mockNotification.setType(NotificationType.SISTEM);
    }

    @Test
    void testSendNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        // UPDATE: Parameter method ditambah 'title' sesuai refactor sebelumnya
        Notification result = notificationService.sendNotification(mockUser, "Donasi Diterima", "Donasi Anda telah diklaim!", donationId, NotificationType.SISTEM);

        assertNotNull(result);
        assertEquals("Donasi Diterima", result.getTitle()); // Cek kelancaran title
        assertEquals("Donasi Anda telah diklaim!", result.getMessageBody());
        assertEquals(donationId, result.getRelatedDonationId());
        assertFalse(result.isRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testGetUserNotifications_NormalScenario() {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Notification> mockSlice = new SliceImpl<>(Collections.singletonList(mockNotification));
        
        when(notificationRepository.findByUser_UsernameOrderByTimeStampDesc(eq("Diddybluds"), any(Pageable.class)))
            .thenReturn(mockSlice);

        Slice<Notification> results = notificationService.getUserNotifications("Diddybluds", "ALL", pageable);

        assertFalse(results.getContent().isEmpty());
        assertEquals(1, results.getContent().size());
        assertEquals("Diddybluds", results.getContent().get(0).getUser().getUsername());
    }

    @Test
    void testGetUserNotifications_AlternativeScenario_EmptyInbox() {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Notification> emptySlice = new SliceImpl<>(Collections.emptyList());
        
        when(notificationRepository.findByUser_UsernameOrderByTimeStampDesc(eq("UserKosong"), any(Pageable.class)))
            .thenReturn(emptySlice);

        Slice<Notification> results = notificationService.getUserNotifications("UserKosong", "ALL", pageable);

        assertTrue(results.getContent().isEmpty());
        assertEquals(0, results.getContent().size());
    }

    @Test
    void testMarkAsRead_FailureScenario_NotFound() {
        UUID fakeId = UUID.randomUUID();
        when(notificationRepository.findById(fakeId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationService.markAsRead(fakeId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testMarkAsRead_SuccessScenario() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(mockNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        Notification result = notificationService.markAsRead(notificationId);

        assertTrue(result.isRead());
        verify(notificationRepository, times(1)).save(mockNotification);
    }
}
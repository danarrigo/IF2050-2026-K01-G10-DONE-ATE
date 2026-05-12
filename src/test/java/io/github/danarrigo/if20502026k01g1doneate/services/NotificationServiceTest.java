package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Notification;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.repositories.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        
        mockNotification = new Notification("Donasi Anda telah diklaim!", LocalDateTime.now(), mockUser);
        mockNotification.setNotificationId(notificationId);
        mockNotification.setRelatedDonationId(donationId);
    }

    // (1) Uji Skenario Berhasil Mengirim Notifikasi
    @Test
    void testSendNotification() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        Notification result = notificationService.sendNotification(mockUser, "Donasi Anda telah diklaim!", donationId);

        assertNotNull(result);
        assertEquals("Donasi Anda telah diklaim!", result.getMessageBody());
        assertEquals(donationId, result.getRelatedDonationId());
        assertFalse(result.isRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // (2) Uji SKPL: Skenario Normal (Menampilkan daftar notifikasi)
    @Test
    void testGetUserNotifications_NormalScenario() {
        when(notificationRepository.findByUser_UsernameOrderByTimeStampDesc("Diddybluds"))
            .thenReturn(Arrays.asList(mockNotification));

        List<Notification> results = notificationService.getUserNotifications("Diddybluds");

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Diddybluds", results.get(0).getUser().getUsername());
    }

    // (3) Uji SKPL: Skenario Alternatif (Kotak Masuk Kosong)
    @Test
    void testGetUserNotifications_AlternativeScenario_EmptyInbox() {
        when(notificationRepository.findByUser_UsernameOrderByTimeStampDesc("UserKosong"))
            .thenReturn(Collections.emptyList());

        List<Notification> results = notificationService.getUserNotifications("UserKosong");

        assertTrue(results.isEmpty());
        assertEquals(0, results.size());
    }

    // (4) Uji Skenario Gagal: Error Handling saat ID Notifikasi Tidak Ditemukan
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

    // (5) Uji Skenario Berhasil: Tandai sudah dibaca
    @Test
    void testMarkAsRead_SuccessScenario() {
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(mockNotification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(mockNotification);

        Notification result = notificationService.markAsRead(notificationId);

        assertTrue(result.isRead());
        verify(notificationRepository, times(1)).save(mockNotification);
    }
}
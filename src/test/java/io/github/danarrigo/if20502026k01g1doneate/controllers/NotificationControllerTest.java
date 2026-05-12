package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.entities.Notification;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class NotificationControllerTest {

    private MockMvc mockMvc;
    private NotificationService notificationService;

    private Notification mockNotification;
    private UUID notificationId;
    private UUID donationId;
    private User mockUser;

    @BeforeEach
    void setUp() {
        notificationService = mock(NotificationService.class);
        
        NotificationController notificationController = new NotificationController(notificationService);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();

        mockUser = new User() {};
        mockUser.setUsername("Dani");

        notificationId = UUID.randomUUID();
        donationId = UUID.randomUUID();
        
        mockNotification = new Notification("Test Notifikasi SKPL", LocalDateTime.now(), mockUser);
        mockNotification.setNotificationId(notificationId);
        mockNotification.setRelatedDonationId(donationId);
    }

    // (1) Test Endpoint GET (SKPL: Skenario Normal - Ada Notifikasi)
    @Test
    void testGetUserNotifications_NotEmpty() throws Exception {
        when(notificationService.getUserNotifications("Dani")).thenReturn(Arrays.asList(mockNotification));

        mockMvc.perform(get("/api/notifications/user/Dani"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].messageBody").value("Test Notifikasi SKPL"))
                .andExpect(jsonPath("$[0].relatedDonationId").value(donationId.toString()))
                .andExpect(jsonPath("$[0].user.username").value("Dani"));
    }

    // (2) Test Endpoint GET (SKPL: Skenario Alternatif - Kotak Masuk Kosong)
    @Test
    void testGetUserNotifications_Empty() throws Exception {
        when(notificationService.getUserNotifications("UserKosong")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/notifications/user/UserKosong"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty()); // Memastikan balasan JSON adalah array kosong []
    }

    // (3) Test Endpoint PUT (Skenario Normal - Tandai Dibaca)
    @Test
    void testMarkNotificationAsRead_Success() throws Exception {
        mockNotification.setRead(true);
        when(notificationService.markAsRead(notificationId)).thenReturn(mockNotification);

        mockMvc.perform(put("/api/notifications/{id}/read", notificationId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    // (4) Test Endpoint PUT (Skenario Gagal - ID Notifikasi Tidak Ada / 404 Not Found)
    @Test
    void testMarkNotificationAsRead_NotFound() throws Exception {
        UUID fakeId = UUID.randomUUID();
        
        when(notificationService.markAsRead(fakeId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Notifikasi tidak ditemukan!"));

        mockMvc.perform(put("/api/notifications/{id}/read", fakeId.toString()))
                .andExpect(status().isNotFound());
    }

    // (5) Test Endpoint DELETE
    @Test
    void testDeleteNotification() throws Exception {
        doNothing().when(notificationService).deleteNotification(notificationId);

        mockMvc.perform(delete("/api/notifications/{id}", notificationId.toString()))
                .andExpect(status().isNoContent()); // Memastikan response HTTP 204 No Content
    }
}
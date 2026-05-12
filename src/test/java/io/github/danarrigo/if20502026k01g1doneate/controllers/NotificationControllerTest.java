package io.github.danarrigo.if20502026k01g1doneate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.danarrigo.if20502026k01g1doneate.entities.Notification;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.enums.NotificationType;
import io.github.danarrigo.if20502026k01g1doneate.services.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
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

    // Buat class konkret pengganti anonymous class untuk menghindari error konversi JSON
    class DummyUser extends User {
        public DummyUser() {}
    }

    @BeforeEach
    void setUp() {
        notificationService = mock(NotificationService.class);
        
        NotificationController notificationController = new NotificationController(notificationService);
        
        // Konfigurasi ObjectMapper agar paham cara membaca tipe tanggal LocalDateTime
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Bekali MockMvc dengan kemampuan membaca parameter Pageable dan menulis JSON
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        mockUser = new DummyUser();
        mockUser.setUsername("Dani");

        notificationId = UUID.randomUUID();
        donationId = UUID.randomUUID();
        
        mockNotification = new Notification();
        mockNotification.setNotificationId(notificationId);
        mockNotification.setTitle("Pemberitahuan Sistem");
        mockNotification.setMessageBody("Test Notifikasi SKPL");
        mockNotification.setTimeStamp(LocalDateTime.now());
        mockNotification.setUser(mockUser);
        mockNotification.setRelatedDonationId(donationId);
        mockNotification.setType(NotificationType.SISTEM);
    }

    @Test
    void testGetUserNotifications_NotEmpty() throws Exception {
        // Gunakan PageRequest.of() eksplisit, jangan biarkan kosong (unpaged)
        Slice<Notification> slice = new SliceImpl<>(List.of(mockNotification), PageRequest.of(0, 10), false);
        
        when(notificationService.getUserNotifications(eq("Dani"), anyString(), any(PageRequest.class)))
                .thenReturn(slice);

        mockMvc.perform(get("/api/notifications/user/Dani")
                .param("filter", "ALL")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value("Pemberitahuan Sistem"))
                .andExpect(jsonPath("$.content[0].messageBody").value("Test Notifikasi SKPL"))
                .andExpect(jsonPath("$.content[0].relatedDonationId").value(donationId.toString()));    }

    @Test
    void testGetUserNotifications_Empty() throws Exception {
        // Gunakan PageRequest.of() eksplisit untuk menghindari UnsupportedOperationException
        Slice<Notification> emptySlice = new SliceImpl<>(List.of(), PageRequest.of(0, 10), false);
        
        when(notificationService.getUserNotifications(eq("UserKosong"), anyString(), any(PageRequest.class)))
                .thenReturn(emptySlice);

        mockMvc.perform(get("/api/notifications/user/UserKosong")
                .param("filter", "ALL")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isEmpty()); 
    }

    @Test
    void testMarkNotificationAsRead_Success() throws Exception {
        mockNotification.setRead(true);
        when(notificationService.markAsRead(notificationId)).thenReturn(mockNotification);

        mockMvc.perform(put("/api/notifications/{id}/read", notificationId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    void testMarkNotificationAsRead_NotFound() throws Exception {
        UUID fakeId = UUID.randomUUID();
        
        when(notificationService.markAsRead(fakeId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Notifikasi tidak ditemukan!"));

        mockMvc.perform(put("/api/notifications/{id}/read", fakeId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteNotification() throws Exception {
        doNothing().when(notificationService).deleteNotification(notificationId);

        mockMvc.perform(delete("/api/notifications/{id}", notificationId.toString()))
                .andExpect(status().isNoContent()); 
    }
}
package io.github.danarrigo.if20502026k01g1doneate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.services.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DonationControllerTest {

    private MockMvc mockMvc;
    private DonationService donationService;
    private ObjectMapper objectMapper;

    private Donation donation;
    private UUID donationId;

    @BeforeEach
    void setUp() {
        donationService = mock(DonationService.class);
        DonationController donationController = new DonationController(donationService);
        mockMvc = MockMvcBuilders.standaloneSetup(donationController).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        donationId = UUID.randomUUID();
        Dish dish = new Dish("Pasta", "path/to/pasta");
        Donator donator = new Donator();
        donation = new Donation(dish, LocalTime.now(), LocalTime.now().minusHours(1), "Waiting for QC", donator);
    }

    @Test
    void testGetAllDonations() throws Exception {
        when(donationService.getAllDonations()).thenReturn(Arrays.asList(donation));

        mockMvc.perform(get("/api/donations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value("Waiting for QC"));
    }

    @Test
    void testGetDonationsByOngoing() throws Exception {
        when(donationService.getDonationsByOngoing(true)).thenReturn(Arrays.asList(donation));

        mockMvc.perform(get("/api/donations/condition/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("Waiting for QC"));
    }

    @Test
    void testGetDonationById() throws Exception {
        when(donationService.getDonationById(donationId)).thenReturn(donation);

        mockMvc.perform(get("/api/donations/{id}", donationId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Waiting for QC"));
    }

    @Test
    void testUpdateDonation() throws Exception {
        Donation updatedDonation = new Donation(donation.getDish(), donation.getTimeAdded(), donation.getTimeCooked(),
                "Approved", new Donator());

        when(donationService.updateDonation(eq(donationId), any(Donation.class))).thenReturn(updatedDonation);

        mockMvc.perform(put("/api/donations/{id}", donationId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDonation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Approved"));
    }

    @Test
    void testCreateDonation() throws Exception {
        when(donationService.createDonation(any(Donation.class))).thenReturn(donation);

        mockMvc.perform(post("/api/donations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(donation)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Waiting for QC"));
    }

    @Test
    void testDeleteAllDonations() throws Exception {
        doNothing().when(donationService).deleteDonations();

        mockMvc.perform(delete("/api/donations"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteDonationByUuid() throws Exception {
        doNothing().when(donationService).deleteDonationByUuid(donationId);

        mockMvc.perform(delete("/api/donations/{id}", donationId.toString()))
                .andExpect(status().isNoContent());
    }
}

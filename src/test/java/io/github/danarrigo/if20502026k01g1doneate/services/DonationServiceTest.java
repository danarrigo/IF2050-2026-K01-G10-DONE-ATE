package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private DonationService donationService;

    private Donation donation;
    private Dish dish;
    private Donator donator;
    private UUID donationId;

    @BeforeEach
    void setUp() {
        dish = new Dish("Pasta", "path/to/pasta");
        donator = new Donator();
        donationId = UUID.randomUUID();

        donation = new Donation(dish, LocalTime.now(), LocalTime.now().minusHours(1), "Waiting for QC", donator);
    }

    @Test
    void testProcessDonation() {
        when(donationRepository.save(any(Donation.class))).thenAnswer(i -> i.getArguments()[0]);

        donationService.processDonation("Pasta", "path/to/pasta", LocalTime.now().minusHours(1), donator);

        verify(donationRepository, times(1)).save(any(Donation.class));
    }

    @Test
    void testCreateDonation() {
        when(donationRepository.save(any(Donation.class))).thenReturn(donation);

        Donation created = donationService.createDonation(donation);

        assertNotNull(created);
        assertEquals("Waiting for QC", created.getStatus());
        verify(donationRepository, times(1)).save(donation);
    }

    @Test
    void testGetAllDonations() {
        when(donationRepository.findAll()).thenReturn(Arrays.asList(donation));

        List<Donation> result = donationService.getAllDonations();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(donationRepository, times(1)).findAll();
    }

    @Test
    void testGetDonationById_Found() {
        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));

        Donation found = donationService.getDonationById(donationId);

        assertNotNull(found);
        assertEquals(donation.getStatus(), found.getStatus());
        verify(donationRepository, times(1)).findById(donationId);
    }

    @Test
    void testGetDonationById_NotFound() {
        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            donationService.getDonationById(donationId);
        });

        assertEquals("Donation not found with id: " + donationId, exception.getMessage());
    }

    @Test
    void testUpdateDonation() {
        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));
        when(donationRepository.save(any(Donation.class))).thenReturn(donation);

        Donation updatedInfo = new Donation(dish, LocalTime.now(), LocalTime.now(), "Approved", donator);
        updatedInfo.setOngoing(false);

        Donation result = donationService.updateDonation(donationId, updatedInfo);

        assertNotNull(result);
        assertEquals("Approved", result.getStatus());
        assertFalse(result.isOngoing());
        verify(donationRepository, times(1)).findById(donationId);
        verify(donationRepository, times(1)).save(any(Donation.class));
    }

    @Test
    void testDeleteDonationByUuid() {
        doNothing().when(donationRepository).deleteById(donationId);

        donationService.deleteDonationByUuid(donationId);

        verify(donationRepository, times(1)).deleteById(donationId);
    }
}

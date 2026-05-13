package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.dtos.CatalogItemRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.CatalogItemResponse;
import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DishRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private DishRepository dishRepository;

    @Mock
    private DonatorRepository donatorRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CatalogService catalogService;

    private Donator donator;
    private Dish dish;
    private Donation donation;
    private UUID donationId;

    @BeforeEach
    void setUp() {
        donator = new Donator();
        donator.setUsername("testuser");

        dish = new Dish("Test Dish", "path/to/image", Duration.ofHours(2));
        donationId = UUID.randomUUID();

        donation = new Donation(dish, LocalDateTime.now(), LocalDateTime.now().minusHours(1), "QC Pending", donator);
        donation.setDonationId(donationId);
        donation.setOngoing(true);
    }

    @Test
    void testGetActiveCatalog() {
        when(donationRepository.findByOngoing(true)).thenReturn(Arrays.asList(donation));

        List<CatalogItemResponse> result = catalogService.getActiveCatalog();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Dish", result.get(0).getDishName());
        verify(donationRepository, times(1)).findByOngoing(true);
    }

    @Test
    void testGetDonatorCatalog() {
        when(donationRepository.findByDonator_Username("testuser")).thenReturn(Arrays.asList(donation));

        List<CatalogItemResponse> result = catalogService.getDonatorCatalog("testuser");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getDonatorUsername());
        verify(donationRepository, times(1)).findByDonator_Username("testuser");
    }

    @Test
    void testAddToCatalog() {
        CatalogItemRequest request = new CatalogItemRequest();
        request.setDishName("New Dish");
        request.setImagePath("new/path");
        request.setExpiresInMinutes(120);
        request.setTimeCooked(LocalDateTime.now());

        when(donatorRepository.findByUsername("testuser")).thenReturn(Optional.of(donator));
        when(dishRepository.save(any(Dish.class))).thenAnswer(i -> i.getArguments()[0]);
        when(donationRepository.save(any(Donation.class))).thenAnswer(i -> i.getArguments()[0]);

        CatalogItemResponse response = catalogService.addToCatalog("testuser", request);

        assertNotNull(response);
        assertEquals("New Dish", response.getDishName());
        assertEquals("QC Pending", response.getStatus());
        assertTrue(response.isOngoing());
        verify(donatorRepository, times(1)).findByUsername("testuser");
        verify(dishRepository, times(1)).save(any(Dish.class));
        verify(donationRepository, times(1)).save(any(Donation.class));
    }

    @Test
    void testUpdateCatalogItem() {
        CatalogItemRequest request = new CatalogItemRequest();
        request.setDishName("Updated Dish");
        request.setImagePath("updated/path");
        request.setExpiresInMinutes(60);
        request.setTimeCooked(LocalDateTime.now());

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));
        when(dishRepository.save(any(Dish.class))).thenAnswer(i -> i.getArguments()[0]);
        when(donationRepository.save(any(Donation.class))).thenAnswer(i -> i.getArguments()[0]);

        CatalogItemResponse response = catalogService.updateCatalogItem(donationId, request);

        assertNotNull(response);
        assertEquals("Updated Dish", response.getDishName());
        assertEquals(60, response.getExpiresInMinutes());
        verify(donationRepository, times(1)).findById(donationId);
        verify(dishRepository, times(1)).save(any(Dish.class));
        verify(donationRepository, times(1)).save(any(Donation.class));
    }

    @Test
    void testRemoveFromCatalog() {
        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));
        when(donationRepository.save(any(Donation.class))).thenReturn(donation);

        catalogService.removeFromCatalog(donationId);

        assertFalse(donation.isOngoing());
        assertEquals("Removed", donation.getStatus());
        verify(donationRepository, times(1)).findById(donationId);
        verify(donationRepository, times(1)).save(donation);
    }

    @Test
    void testRemoveFromCatalog_NotFound() {
        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> catalogService.removeFromCatalog(donationId));
        verify(donationRepository, times(1)).findById(donationId);
        verify(donationRepository, never()).save(any());
    }
}

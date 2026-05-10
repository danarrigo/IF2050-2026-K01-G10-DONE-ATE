package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.dtos.QCFormData;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DishRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private DishService dishService;

    private QCFormData formData;
    private Donation donation;
    private UUID dishId;

    @BeforeEach
    void setUp() {
        dishId = UUID.randomUUID();

        formData = new QCFormData();
        formData.setDishId(dishId);
        
        donation = new Donation();
        donation.setOngoing(false);
        donation.setStatus("Waiting for QC");
    }

    @Test
    void testDishQualityControl_Passed() {
        // Arrange
        formData.setFreshScent(true);
        formData.setNoSpoilage(true);
        formData.setProperlyCooked(true);
        formData.setHasExpiredIngredients(false);
        formData.setNoForeignObjects(true);
        formData.setSafeTemperature(true);
        formData.setNormalColor(true);
        formData.setGoodTexture(true);
        formData.setSafeToEat(true);
        formData.setPresentable(true);

        when(donationRepository.findByDish_DishId(dishId)).thenReturn(Optional.of(donation));
        when(donationRepository.save(any(Donation.class))).thenReturn(donation);

        // Act
        boolean result = dishService.dishQualityControl(formData);

        // Assert
        assertTrue(result);
        assertTrue(donation.isOngoing());
        assertEquals("QC Passed", donation.getStatus());
        verify(donationRepository, times(1)).save(donation);
    }

    @Test
    void testDishQualityControl_Failed() {
        // Arrange
        formData.setFreshScent(true);
        // Fail on one condition
        formData.setHasExpiredIngredients(true);

        when(donationRepository.findByDish_DishId(dishId)).thenReturn(Optional.of(donation));
        when(donationRepository.save(any(Donation.class))).thenReturn(donation);

        // Act
        boolean result = dishService.dishQualityControl(formData);

        // Assert
        assertFalse(result);
        assertFalse(donation.isOngoing());
        assertEquals("QC Failed", donation.getStatus());
        verify(donationRepository, times(1)).save(donation);
    }

    @Test
    void testDishQualityControl_DonationNotFound() {
        // Arrange
        when(donationRepository.findByDish_DishId(dishId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dishService.dishQualityControl(formData);
        });
        
        assertEquals("Donation not found for dish id: " + dishId, exception.getMessage());
        verify(donationRepository, never()).save(any());
    }
}

package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.dtos.CatalogItemRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.CatalogItemResponse;
import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DishRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonatorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CatalogService {

    private final DonationRepository donationRepository;
    private final DishRepository dishRepository;
    private final DonatorRepository donatorRepository;

    public CatalogService(DonationRepository donationRepository,
                          DishRepository dishRepository,
                          DonatorRepository donatorRepository) {
        this.donationRepository = donationRepository;
        this.dishRepository = dishRepository;
        this.donatorRepository = donatorRepository;
    }

    public List<CatalogItemResponse> getActiveCatalog() {
        return donationRepository.findByOngoing(true)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<CatalogItemResponse> getDonatorCatalog(String username) {
        return donationRepository.findByDonator_Username(username)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CatalogItemResponse addToCatalog(String donatorUsername, CatalogItemRequest request) {
        Donator donator = donatorRepository.findByUsername(donatorUsername)
                .orElseThrow(() -> new RuntimeException("Donator not found: " + donatorUsername));

        Dish dish = new Dish(
                request.getDishName(),
                request.getImagePath(),
                Duration.ofMinutes(request.getExpiresInMinutes())
        );
        dishRepository.save(dish);

        Donation donation = new Donation(
                dish,
                LocalDateTime.now(),
                request.getTimeCooked(),
                "QC Pending",
                donator
        );
        donation.setOngoing(true);
        donationRepository.save(donation);

        return toResponse(donation);
    }

    public CatalogItemResponse updateCatalogItem(UUID donationId, CatalogItemRequest request) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found: " + donationId));

        Dish dish = donation.getDish();
        dish.setName(request.getDishName());
        dish.setImagePath(request.getImagePath());
        dish.setExpiresIn(Duration.ofMinutes(request.getExpiresInMinutes()));
        dishRepository.save(dish);

        donation.setTimeCooked(request.getTimeCooked());
        donationRepository.save(donation);

        return toResponse(donation);
    }

    public void removeFromCatalog(UUID donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found: " + donationId));
        donation.setOngoing(false);
        donation.setStatus("Removed");
        donationRepository.save(donation);
    }

    private CatalogItemResponse toResponse(Donation donation) {
        CatalogItemResponse response = new CatalogItemResponse();
        response.setDonationId(donation.getDonationId());
        response.setTimeAdded(donation.getTimeAdded());
        response.setTimeCooked(donation.getTimeCooked());
        response.setTaken(donation.isTaken());
        response.setOngoing(donation.isOngoing());
        response.setStatus(donation.getStatus());

        if (donation.getDonator() != null) {
            response.setDonatorUsername(donation.getDonator().getUsername());
        }
        if (donation.getDish() != null) {
            Dish dish = donation.getDish();
            response.setDishId(dish.getDishId());
            response.setDishName(dish.getName());
            response.setImagePath(dish.getImagePath());
            response.setExpiresInMinutes(
                    dish.getExpiresIn() != null ? dish.getExpiresIn().toMinutes() : 0
            );
        }
        return response;
    }
}

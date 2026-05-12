package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DonationService {
    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public void processDonation(String dishName, String dishPath, LocalDateTime timeCooked, Donator donator) {
        Dish dish = new Dish(dishName, dishPath);
        Donation donation = new Donation(dish, LocalDateTime.now(), timeCooked, "Waiting for QC", donator);
        createDonation(donation);
        System.out.println("Donation processed, now waiting for QC");
    }

    public void removeDonation(UUID donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found with id: " + donationId));
        donation.setOngoing(false);
        donation.setStatus("Removed");
        donationRepository.save(donation);
        System.out.println("Donation removed");
    }

    // CREATE
    public Donation createDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    // READ
    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public List<Donation> getDonationsByOngoing(boolean ongoing) {
        return donationRepository.findByOngoing(ongoing);
    }

    public Donation getDonationById(UUID id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found with id: " + id));
    }

    // UPDATE

    public Donation updateDonation(UUID id, Donation donation) {
        Donation existingDonation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found with id: " + id));
        existingDonation.setTaken(donation.isTaken());
        existingDonation.setDish(donation.getDish());
        existingDonation.setTimeAdded(donation.getTimeAdded());
        existingDonation.setTimeCooked(donation.getTimeCooked());
        existingDonation.setOngoing(donation.isOngoing());
        existingDonation.setStatus(donation.getStatus());
        return donationRepository.save(existingDonation);

    }

    // DELETE
    public void deleteDonations() {
        donationRepository.deleteAll();
    }

    public void deleteDonationByUuid(UUID uuid) {
        donationRepository.deleteById(uuid);
    }

}

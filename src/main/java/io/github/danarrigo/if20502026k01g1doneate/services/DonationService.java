package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DonationService {
    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public void removeDonation(UUID donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found with id: " + donationId));
        donation.setOngoing(false);
        donation.setStatus("Selesai");
        donationRepository.save(donation);
    }

    public void cancelDonation(UUID donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found with id: " + donationId));
        donation.setOngoing(false);
        donation.setStatus("Dibatalkan");
        donationRepository.save(donation);
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

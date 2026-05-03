package io.github.danarrigo.if20502026k01g1doneate.services;


import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationService {
    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public List<Donation> getAllDonations(){
        return donationRepository.findAll();
    }

    public Donation createDonation(Donation donation){
        return donationRepository.save(donation);
    }
}

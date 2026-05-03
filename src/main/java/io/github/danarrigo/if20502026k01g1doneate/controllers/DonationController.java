package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.services.DonationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/donations")
public class DonationController {
    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    //GET
    @GetMapping
    public ResponseEntity<List<Donation>> getAllDonations() {
        List<Donation> donationList =  donationService.getAllDonations();
        return ResponseEntity.ok(donationList);
    }

    @GetMapping("/condition/{ongoing}")
    public ResponseEntity<List<Donation>> getDonationsByOngoing(@PathVariable boolean ongoing){
        List<Donation> donationList = donationService.getDonationsByOngoing(ongoing);
        return ResponseEntity.ok(donationList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Donation> getDonationById(@PathVariable String id){
        Donation donation = donationService.getDonationById(java.util.UUID.fromString(id));
        return ResponseEntity.ok(donation);
    }

    //PUT
    @PutMapping("/{id}")
    public ResponseEntity<Donation> updateDonation(@PathVariable String id, @RequestBody Donation donation){
        Donation updatedDonation = donationService.updateDonation(java.util.UUID.fromString(id), donation);
        return ResponseEntity.ok(updatedDonation);
    }

    //POST
    @PostMapping
    public ResponseEntity<Donation> createDonation(@RequestBody Donation donation){
        Donation addedDonation = donationService.createDonation(donation);
        return ResponseEntity.ok(addedDonation);
    }


    //DELETE
    @DeleteMapping
    public ResponseEntity<Void> deleteAllDonations(){
        donationService.deleteDonations();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonationByUuid(@PathVariable String id){
        donationService.deleteDonationByUuid(java.util.UUID.fromString(id));
        return ResponseEntity.noContent().build();
    }




}



package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.entities.Dish;
import io.github.danarrigo.if20502026k01g1doneate.repositories.DishRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import io.github.danarrigo.if20502026k01g1doneate.repositories.DonationRepository;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import io.github.danarrigo.if20502026k01g1doneate.dtos.QCFormData;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DishService {
    public final DishRepository dishRepository;
    public final DonationRepository donationRepository;

    public DishService(DishRepository dishRepository, DonationRepository donationRepository) {
        this.dishRepository = dishRepository;
        this.donationRepository = donationRepository;
    }

    public boolean dishQualityControl(QCFormData body) {
        Donation donation = donationRepository.findByDish_DishId(body.getDishId())
                .orElseThrow(() -> new RuntimeException("Donation not found for dish id: " + body.getDishId()));

        if (body.isPassed()) {
            donation.setOngoing(true);
            donation.setStatus("QC Passed");
        } else {
            donation.setOngoing(false);
            donation.setStatus("QC Failed");
        }
        donationRepository.save(donation);
        return body.isPassed();
    }

    // CREATE
    public Dish createDish(Dish dish) {
        return dishRepository.save(dish);
    }

    // READ
    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public Dish getDishById(UUID id) {
        return dishRepository.findById(id).orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));
    }

    // UPDATE
    public Dish updateDish(Dish newDish, UUID id) {
        Dish oldDish = dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));
        oldDish.setName(newDish.getName());
        oldDish.setImagePath(newDish.getImagePath());
        return dishRepository.save(oldDish);
    }

    // DELETE
    public void deleteDishes() {
        dishRepository.deleteAll();
    }

    public void deleteDishById(UUID id) {
        dishRepository.deleteById(id);
    }
}

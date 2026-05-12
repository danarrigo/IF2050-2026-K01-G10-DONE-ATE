package io.github.danarrigo.if20502026k01g1doneate.repositories;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DonationRepository extends JpaRepository<Donation, UUID> {
    public List<Donation> findByOngoing(boolean ongoing);
    public Optional<Donation> findByDish_DishId(UUID dishId);
    public List<Donation> findByDonator_Username(String username);
    public List<Donation> findByDonator_UsernameAndOngoing(String username, boolean ongoing);
}

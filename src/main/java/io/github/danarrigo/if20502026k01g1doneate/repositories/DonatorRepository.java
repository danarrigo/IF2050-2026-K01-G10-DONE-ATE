package io.github.danarrigo.if20502026k01g1doneate.repositories;

import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.enums.DonatorType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DonatorRepository extends JpaRepository<Donator, String> {
    Optional<Donator> findByUsername(String username);
    Optional<Donator> findByDonatorId(UUID donatorId);
    List<Donator> findByDonatorType(DonatorType donatorType);
}

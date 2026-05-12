package io.github.danarrigo.if20502026k01g1doneate.repositories;

import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.enums.RecipientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipientRepository extends JpaRepository<Recipient, String> {
    Optional<Recipient> findByUsername(String username);
    List<Recipient> findByRecipientType(RecipientType recipientType);
}

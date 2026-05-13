package io.github.danarrigo.if20502026k01g1doneate.services;

import io.github.danarrigo.if20502026k01g1doneate.dtos.DonatorRegistrationRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.LoginRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.RecipientRegistrationRequest;
import io.github.danarrigo.if20502026k01g1doneate.entities.Donator;
import io.github.danarrigo.if20502026k01g1doneate.entities.Recipient;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.enums.UserRole;
import io.github.danarrigo.if20502026k01g1doneate.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, String> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return Map.of("username", user.getUsername(), "role", UserRole.of(user).name());
    }

    public Map<String, String> registerDonator(DonatorRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        Donator donator = new Donator(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getAddress(),
                request.getPhoneNumber(),
                request.getEmail(),
                request.getDonatorType() // Menghapus ArrayList yang sudah usang
        );
        userRepository.save(donator);
        return Map.of("username", donator.getUsername(), "role", UserRole.DONATOR.name());
    }

    public Map<String, String> registerRecipient(RecipientRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }
        Recipient recipient = new Recipient();
        recipient.setUsername(request.getUsername());
        recipient.setPassword(passwordEncoder.encode(request.getPassword()));
        recipient.setAddress(request.getAddress());
        recipient.setPhoneNumber(request.getPhoneNumber());
        recipient.setEmail(request.getEmail());
        recipient.setFullName(request.getFullName());
        recipient.setOperationalTimeStart(request.getOperationalTimeStart());
        recipient.setOperationalTimeEnd(request.getOperationalTimeEnd());
        recipient.setRecipientType(request.getRecipientType());
        userRepository.save(recipient);
        return Map.of("username", recipient.getUsername(), "role", UserRole.RECIPIENT.name());
    }
}
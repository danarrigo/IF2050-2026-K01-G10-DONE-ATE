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
import io.github.danarrigo.if20502026k01g1doneate.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public Map<String, String> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        
        String token = jwtUtils.generateToken(user.getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("role", UserRole.of(user).name());
        response.put("token", token);
        return response;
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
        
        String token = jwtUtils.generateToken(donator.getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("username", donator.getUsername());
        response.put("role", UserRole.DONATOR.name());
        response.put("token", token);
        return response;
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
        
        String token = jwtUtils.generateToken(recipient.getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("username", recipient.getUsername());
        response.put("role", UserRole.RECIPIENT.name());
        response.put("token", token);
        return response;
    }
}
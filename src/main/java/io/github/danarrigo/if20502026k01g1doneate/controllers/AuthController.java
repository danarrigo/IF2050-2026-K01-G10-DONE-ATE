package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.dtos.DonatorRegistrationRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.LoginRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.RecipientRegistrationRequest;
import io.github.danarrigo.if20502026k01g1doneate.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register/donator")
    public ResponseEntity<?> registerDonator(@RequestBody DonatorRegistrationRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerDonator(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register/recipient")
    public ResponseEntity<?> registerRecipient(@RequestBody RecipientRegistrationRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerRecipient(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

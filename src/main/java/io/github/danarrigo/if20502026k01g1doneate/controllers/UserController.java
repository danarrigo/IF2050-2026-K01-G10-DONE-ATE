package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.dtos.UserProfileUpdateDTO;
import io.github.danarrigo.if20502026k01g1doneate.entities.User;
import io.github.danarrigo.if20502026k01g1doneate.services.UserService;
import io.github.danarrigo.if20502026k01g1doneate.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@RequestHeader("Authorization") String token) {
        String username = extractUsername(token);
        try {
            return ResponseEntity.ok(userService.getProfile(username));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@RequestHeader("Authorization") String token,
                                             @RequestBody UserProfileUpdateDTO request) {
        String username = extractUsername(token);
        try {
            return ResponseEntity.ok(userService.updateProfile(username, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private String extractUsername(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return jwtUtils.extractUsername(token.substring(7));
        }
        return null;
    }
}

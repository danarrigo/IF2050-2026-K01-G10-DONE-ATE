package io.github.danarrigo.if20502026k01g1doneate.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @GetMapping("/user-data")
    public ResponseEntity<String> loadUserData() {
        // Placeholder for user data loading logic
        return ResponseEntity.ok("User data loaded");
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmTransaction() {
        // Placeholder for transaction confirmation logic
        return ResponseEntity.ok("Transaction confirmed successfully!");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> handleLogoutAction() {
        // Placeholder for logout logic
        return ResponseEntity.ok("Logout successful");
    }
}

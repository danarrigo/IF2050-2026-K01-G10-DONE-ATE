package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.dtos.ClaimRequestDTO;
import io.github.danarrigo.if20502026k01g1doneate.services.ClaimingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
public class ClaimingController {

    private final ClaimingService claimingService;

    public ClaimingController(ClaimingService claimingService) {
        this.claimingService = claimingService;
    }

    @PostMapping
    public ResponseEntity<String> claimDonation(@RequestBody ClaimRequestDTO request) {
        String result = claimingService.validateClaimability(request.getRecipientUsername(), request.getDonationId());
        
        if ("success".equals(result)) {
            return ResponseEntity.ok("Klaim berhasil diproses.");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/cancel/{transactionCode}")
    public ResponseEntity<String> cancelClaim(@PathVariable Integer transactionCode) {
        String result = claimingService.cancelClaim(transactionCode);
        
        if ("success".equals(result)) {
            return ResponseEntity.ok("Pembatalan klaim berhasil.");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> validateTransactionCode(@RequestBody Map<String, Integer> request) {
        Integer inputCode = request.get("inputCode");
        
        if (inputCode == null) {
            return ResponseEntity.badRequest().body("Kode tidak boleh kosong");
        }

        String result = claimingService.validateTransactionCode(inputCode);
        
        if ("success".equals(result)) {
            // Sesuai diagram: tampilkanStatus("Transaksi Selesai")
            return ResponseEntity.ok("Transaksi Selesai");
        } else {
            // Sesuai diagram: tampilkanPesan("Kode salah, silakan cek kembali dengan Donatur")
            return ResponseEntity.badRequest().body("Kode salah, silakan cek kembali dengan Donatur");
        }
    }
}

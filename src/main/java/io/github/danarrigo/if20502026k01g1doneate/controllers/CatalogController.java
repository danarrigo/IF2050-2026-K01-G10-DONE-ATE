package io.github.danarrigo.if20502026k01g1doneate.controllers;

import io.github.danarrigo.if20502026k01g1doneate.dtos.CatalogItemRequest;
import io.github.danarrigo.if20502026k01g1doneate.dtos.CatalogItemResponse;
import io.github.danarrigo.if20502026k01g1doneate.services.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public ResponseEntity<List<CatalogItemResponse>> getActiveCatalog() {
        return ResponseEntity.ok(catalogService.getActiveCatalog());
    }

    @GetMapping("/donator/{username}")
    public ResponseEntity<?> getDonatorCatalog(@PathVariable String username) {
        try {
            return ResponseEntity.ok(catalogService.getDonatorCatalog(username));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/recipient/{username}")
    public ResponseEntity<?> getRecipientCatalog(@PathVariable String username) {
        try {
            return ResponseEntity.ok(catalogService.getRecipientCatalog(username));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/donator/{username}")
    public ResponseEntity<?> addToCatalog(@PathVariable String username,
                                          @RequestBody CatalogItemRequest request) {
        try {
            CatalogItemResponse response = catalogService.addToCatalog(username, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{donationId}")
    public ResponseEntity<?> updateCatalogItem(@PathVariable UUID donationId,
                                               @RequestBody CatalogItemRequest request) {
        try {
            return ResponseEntity.ok(catalogService.updateCatalogItem(donationId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{donationId}")
    public ResponseEntity<?> removeFromCatalog(@PathVariable UUID donationId) {
        try {
            catalogService.removeFromCatalog(donationId);
            return ResponseEntity.ok(Map.of("message", "Donation removed from catalog"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}

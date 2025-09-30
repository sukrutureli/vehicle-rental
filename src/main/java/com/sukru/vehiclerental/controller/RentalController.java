package com.sukru.vehiclerental.controller;

import com.sukru.vehiclerental.entity.Rental;
import com.sukru.vehiclerental.entity.enums.RentalStatus;
import com.sukru.vehiclerental.repo.RentalRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/rentals")
public class RentalController {

    private final RentalRepo rentalRepo;

    public RentalController(RentalRepo rentalRepo) {
        this.rentalRepo = rentalRepo;
    }

    @GetMapping
    public String rentalsPage() {
        return "rentals"; // rentals.html
    }

    // API
    @GetMapping("/api")
    @ResponseBody
    public List<Rental> listApi() {
        return rentalRepo.findAll();
    }

    @PostMapping("/api")
    @ResponseBody
    public Rental addApi(@RequestBody Rental rental) {
    	LocalDateTime now = LocalDateTime.now();
        rental.setCreatedAt(now);
        rental.setUpdatedAt(now);
        return rentalRepo.save(rental);
    }
    
    // API - Complete
    @PutMapping("/api/{id}/complete")
    @ResponseBody
    public ResponseEntity<Rental> completeRental(@PathVariable UUID id) {
        return rentalRepo.findById(id)
                .map(r -> {
                    r.setStatus(RentalStatus.COMPLETED);
                    r.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(rentalRepo.save(r));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // API - Cancel
    @PutMapping("/api/{id}/cancel")
    @ResponseBody
    public ResponseEntity<Rental> cancelRental(@PathVariable UUID id) {
        return rentalRepo.findById(id)
                .map(r -> {
                    r.setStatus(RentalStatus.CANCELLED);
                    r.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(rentalRepo.save(r));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.sukru.vehiclerental.controller;

import com.sukru.vehiclerental.entity.Vehicle;
import com.sukru.vehiclerental.repo.VehicleRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
public class VehicleController {

    private final VehicleRepo vehicleRepo;

    public VehicleController(VehicleRepo vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
    }

    @GetMapping("/vehicles")
    public String vehiclesPage() {
        return "vehicles"; // vehicles.html
    } 
    
    @GetMapping("/vehicles/edit")
    public String editPage() {
        return "edit-vehicle"; // edit-vehicle.html
    }

    // API

    // Listele
    @GetMapping("/api/vehicles")
    @ResponseBody
    public List<Vehicle> listVehiclesApi() {
        return vehicleRepo.findAll();
    }

    // Ekle
    @PostMapping("/api/vehicles")
    @ResponseBody
    public ResponseEntity<?> addVehicleApi(@RequestBody Vehicle vehicle) {
    	if (vehicleRepo.existsByPlate(vehicle.getPlate())) {
            return ResponseEntity.badRequest().body("Plate already exists: " + vehicle.getPlate());
        }
        LocalDateTime now = LocalDateTime.now();
        vehicle.setCreatedAt(now);
        vehicle.setUpdatedAt(now);
        return ResponseEntity.ok(vehicleRepo.save(vehicle));
    }

    // Guncelle
    @PutMapping("/api/vehicles/{id}")
    @ResponseBody
    public ResponseEntity<?> updateVehicleApi(@PathVariable("id") UUID id,
                                              @RequestBody Vehicle updated) {
        var existingOpt = vehicleRepo.findById(id);

        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            var conflict = vehicleRepo.findByPlate(updated.getPlate())
                    .filter(v -> !v.getId().equals(id));
            if (conflict.isPresent()) {
                return ResponseEntity.badRequest().body("Plate already in use by another vehicle.");
            }

            Vehicle existing = existingOpt.get();
            updated.setId(id);
            updated.setCreatedAt(existing.getCreatedAt()); // createdAt sabit
            updated.setUpdatedAt(LocalDateTime.now());

            Vehicle saved = vehicleRepo.save(updated);
            return ResponseEntity.ok(saved);
        }
    }

    // Sil
    @DeleteMapping("/api/vehicles/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteVehicleApi(@PathVariable UUID id) {
        if (!vehicleRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        } else {
            vehicleRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }
}

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
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleRepo vehicleRepo;

    public VehicleController(VehicleRepo vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
    }

    @GetMapping
    public String vehiclesPage() {
        return "vehicles"; // vehicles.html
    } 
    
    @GetMapping("/edit")
    public String editPage() {
        return "edit-vehicle"; // edit-vehicle.html
    }

    // API

    // Listele
    @GetMapping("/api")
    @ResponseBody
    public List<Vehicle> listVehiclesApi() {
        return vehicleRepo.findAll();
    }

    // Ekle
    @PostMapping("/api")
    @ResponseBody
    public Vehicle addVehicleApi(@RequestBody Vehicle vehicle) {
        LocalDateTime now = LocalDateTime.now();
        vehicle.setCreatedAt(now);
        vehicle.setUpdatedAt(now);
        return vehicleRepo.save(vehicle);
    }

    // Guncelle
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Vehicle> updateVehicleApi(@PathVariable("id") UUID id,
                                                    @RequestBody Vehicle updated) {
        return vehicleRepo.findById(id)
                .map(existing -> {
                    updated.setId(id); // ID sabit kalir
                    updated.setCreatedAt(existing.getCreatedAt()); // createdAt korunur
                    updated.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(vehicleRepo.save(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Sil
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteVehicleApi(@PathVariable UUID id) {
        if (!vehicleRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        vehicleRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

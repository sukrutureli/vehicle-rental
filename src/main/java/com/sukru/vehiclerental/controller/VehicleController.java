package com.sukru.vehiclerental.controller;

import com.sukru.vehiclerental.entity.Vehicle;
import com.sukru.vehiclerental.repo.VehicleRepo;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public List<Vehicle> listVehiclesApi(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String transmission,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime availableFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime availableTo,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        List<Vehicle> all = vehicleRepo.findAll();
        
        //Filter
        List<Vehicle> filtered = all.stream()
                .filter(v -> brand == null || v.getBrand().equalsIgnoreCase(brand))
                .filter(v -> model == null || v.getModel().equalsIgnoreCase(model))
                .filter(v -> city == null || v.getCity().equalsIgnoreCase(city))
                .filter(v -> fuelType == null || v.getFuelType().toString().equalsIgnoreCase(fuelType))
                .filter(v -> transmission == null || v.getTransmission().toString().equalsIgnoreCase(transmission))
                .filter(v -> status == null || v.getStatus().toString().equalsIgnoreCase(status))
                .filter(v -> minPrice == null || v.getDailyPrice() >= minPrice)
                .filter(v -> maxPrice == null || v.getDailyPrice() <= maxPrice)
                .filter(v -> availableFrom == null || !v.getAvailableFrom().isAfter(availableFrom))
                .filter(v -> availableTo == null || !v.getAvailableTo().isBefore(availableTo))
                .collect(Collectors.toList());
        
        //Sort
        if ("dailyPrice".equalsIgnoreCase(sortBy)) {
            if ("desc".equalsIgnoreCase(sortDir)) {
                filtered.sort((a, b) -> b.getDailyPrice().compareTo(a.getDailyPrice()));
            } else {
                filtered.sort((a, b) -> a.getDailyPrice().compareTo(b.getDailyPrice()));
            }
        }

        //Page
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, filtered.size());

        if (fromIndex >= filtered.size()) {
            return List.of();
        }

        return filtered.subList(fromIndex, toIndex);
    }


    // Ekle
    @PostMapping("/api/vehicles")
    @ResponseBody
    public Vehicle addVehicleApi(@RequestBody Vehicle vehicle) {
    	if (vehicleRepo.existsByPlate(vehicle.getPlate())) {
            throw new IllegalArgumentException("Plate already exists: " + vehicle.getPlate());
        }
        LocalDateTime now = LocalDateTime.now();
        vehicle.setCreatedAt(now);
        vehicle.setUpdatedAt(now);
        return vehicleRepo.save(vehicle);
    }

    // Detay
    @GetMapping("/api/vehicles/{id}")
    @ResponseBody
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable UUID id) {
        var opt = vehicleRepo.findById(id);
        if (opt.isEmpty()) {
        	throw new IllegalArgumentException("Vehicle not found with id: " + id);
        }
        return ResponseEntity.ok(opt.get());
    }
    
    // Guncelle
    @PutMapping("/api/vehicles/{id}")
    @ResponseBody
    public Vehicle updateVehicleApi(@PathVariable("id") UUID id,
                                              @RequestBody Vehicle updated) {
        var existingOpt = vehicleRepo.findById(id);

        if (existingOpt.isEmpty()) {
        	throw new IllegalArgumentException("Vehicle not found with id: " + id);
        } else {
            var conflict = vehicleRepo.findByPlate(updated.getPlate())
                    .filter(v -> !v.getId().equals(id));
            if (conflict.isPresent()) {
                throw new IllegalArgumentException("Plate already in use by another vehicle.");
            }

            Vehicle existing = existingOpt.get();
            updated.setId(id);
            updated.setCreatedAt(existing.getCreatedAt());
            updated.setUpdatedAt(LocalDateTime.now());

            return vehicleRepo.save(updated);
        }
    }

    // Sil
    @DeleteMapping("/api/vehicles/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteVehicleApi(@PathVariable UUID id) {
        if (!vehicleRepo.existsById(id)) {
        	 throw new IllegalArgumentException("Vehicle not found with id: " + id);
        } else {
            vehicleRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }
}

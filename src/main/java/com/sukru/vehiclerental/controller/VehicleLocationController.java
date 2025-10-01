package com.sukru.vehiclerental.controller;

import com.sukru.vehiclerental.entity.VehicleLocation;
import com.sukru.vehiclerental.repo.VehicleLocationRepo;
import com.sukru.vehiclerental.repo.VehicleRepo;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class VehicleLocationController {

    private final VehicleLocationRepo locationRepo;
    private final VehicleRepo vehicleRepo;

    public VehicleLocationController(VehicleLocationRepo locationRepo, VehicleRepo vehicleRepo) {
        this.locationRepo = locationRepo;
        this.vehicleRepo = vehicleRepo;
    }

    @GetMapping("/locations")
    public String showLocationsPage() {
        return "locations";
    }

    
    // Tek aracin son konumu
    @GetMapping("/api/vehicles/{id}/location/latest")
    @ResponseBody
    public ResponseEntity<VehicleLocation> getLatestLocation(@PathVariable UUID id) {
        Optional<VehicleLocation> optLocation = locationRepo.findTopByVehicleIdOrderByReportedAtDesc(id);

        if (optLocation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        VehicleLocation location = optLocation.get();

        vehicleRepo.findById(id).ifPresent(vehicle -> {
            location.setVehiclePlate(vehicle.getPlate());
        });

        return ResponseEntity.ok(location);
    }

    // Tum araclarin son konumu
    @GetMapping("/api/vehicles/locations/latest")
    @ResponseBody
    public ResponseEntity<List<VehicleLocation>> getLatestLocationsForAllVehicles() {
        List<VehicleLocation> all = locationRepo.findAll();

        if (all.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // Her vehicleId icin en guncel location
        Map<UUID, VehicleLocation> latestByVehicle = new HashMap<>();
        for (VehicleLocation loc : all) {
            UUID vehicleId = loc.getVehicleId();
            if (!latestByVehicle.containsKey(vehicleId)) {
                latestByVehicle.put(vehicleId, loc);
            } else {
                VehicleLocation existing = latestByVehicle.get(vehicleId);
                if (loc.getReportedAt().isAfter(existing.getReportedAt())) {
                    latestByVehicle.put(vehicleId, loc);
                }
            }
        }

        latestByVehicle.values().forEach(loc -> {
            vehicleRepo.findById(loc.getVehicleId()).ifPresent(vehicle -> {
                loc.setVehiclePlate(vehicle.getPlate());
            });
        });

        return ResponseEntity.ok(new ArrayList<>(latestByVehicle.values()));
    }

}

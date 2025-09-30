package com.sukru.vehiclerental.controller;

import com.sukru.vehiclerental.entity.VehicleLocation;
import com.sukru.vehiclerental.repo.VehicleLocationRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class VehicleLocationController {

    private final VehicleLocationRepo locationRepo;

    public VehicleLocationController(VehicleLocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    @GetMapping("/locations")
    public String showLocationsPage() {
        return "locations";
    }

    
    // Tek aracin son konumu
    @GetMapping("/api/vehicles/{id}/location/latest")
    @ResponseBody
    public ResponseEntity<VehicleLocation> getLatestLocation(@PathVariable UUID id) {
        return locationRepo.findTopByVehicleIdOrderByReportedAtDesc(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Tum araclarin son konumu
    @GetMapping("/api/vehicles/locations/latest")
    @ResponseBody
    public List<VehicleLocation> getLatestLocationsForAllVehicles() {
        List<VehicleLocation> all = locationRepo.findAll();

        // Her vehicleId icin en guncel konum sec
        Map<UUID, VehicleLocation> latestByVehicle = all.stream()
                .collect(Collectors.toMap(
                        VehicleLocation::getVehicleId,
                        loc -> loc,
                        (loc1, loc2) -> loc1.getReportedAt().isAfter(loc2.getReportedAt()) ? loc1 : loc2
                ));

        return new ArrayList<>(latestByVehicle.values());
    }
}

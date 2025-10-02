package com.sukru.vehiclerental.controller;

import com.sukru.vehiclerental.entity.Vehicle;
import com.sukru.vehiclerental.entity.VehicleLocation;
import com.sukru.vehiclerental.repo.VehicleLocationRepo;
import com.sukru.vehiclerental.repo.VehicleRepo;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public VehicleLocation getLatestLocation(@PathVariable UUID id) {
        Optional<VehicleLocation> optLocation = locationRepo.findTopByVehicleIdOrderByReportedAtDesc(id);

        if (optLocation.isEmpty()) {
        	throw new IllegalArgumentException("No location found for vehicle id: " + id);
        }

        VehicleLocation location = optLocation.get();

        vehicleRepo.findById(id).ifPresent(vehicle -> {
            location.setVehiclePlate(vehicle.getPlate());
        });

        return location;
    }

    // Tum araclarin son konumu
    @GetMapping("/api/vehicles/locations/latest")
    @ResponseBody
    public ResponseEntity<List<VehicleLocation>> getLatestLocationsForAllVehicles(
            @RequestParam(required = false) Double nearLat,
            @RequestParam(required = false) Double nearLon,
            @RequestParam(required = false) Double radiusKm) {

        List<VehicleLocation> all = locationRepo.findAll();

        if (all.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        // En güucel konumlar
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

        // Plate bilgisi ekle
        latestByVehicle.values().forEach(loc ->
            vehicleRepo.findById(loc.getVehicleId())
                       .ifPresent(vehicle -> loc.setVehiclePlate(vehicle.getPlate()))
        );

        List<VehicleLocation> result = new ArrayList<>(latestByVehicle.values());

        // Eger radius parametreleri verilmisse filtrele
        if (nearLat != null && nearLon != null && radiusKm != null) {
            result = result.stream()
                    .filter(loc -> {
                        double distance = haversine(nearLat, nearLon, loc.getLat(), loc.getLon());
                        return distance <= radiusKm;
                    })
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/api/telematics/{vehicleId}/location")
    @ResponseBody
    public VehicleLocation addLocation(@PathVariable UUID vehicleId, 
    		@RequestBody VehicleLocation location) {
    	
    	var vehicleOpt = vehicleRepo.findById(vehicleId);
    	if (vehicleOpt.isEmpty()) {
    		throw new IllegalArgumentException("Vehicle not found: " + vehicleId);
    	}
    	
    	location.setVehicleId(vehicleId);
    	location.setVehiclePlate(vehicleOpt.get().getPlate());
    	location.setReportedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    	
        return locationRepo.save(location);
    }

    // Haversine Formula (km cinsinden mesafe)
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }


}

package com.sukru.vehiclerental.service;

import com.sukru.vehiclerental.entity.Vehicle;
import com.sukru.vehiclerental.entity.VehicleLocation;
import com.sukru.vehiclerental.repo.VehicleLocationRepo;
import com.sukru.vehiclerental.repo.VehicleRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class DummyLocationGenerator {

    private final VehicleRepo vehicleRepo;
    private final VehicleLocationRepo locationRepo;
    private final Random random = new Random();

    public DummyLocationGenerator(VehicleRepo vehicleRepo, VehicleLocationRepo locationRepo) {
        this.vehicleRepo = vehicleRepo;
        this.locationRepo = locationRepo;
    }

    // Her dakika calisir
    @Scheduled(fixedRate = 60000)
    public void updateLocations() {
        List<Vehicle> activeVehicles = vehicleRepo.findAll()
                .stream()
                .filter(v -> !"MAINTENANCE".equalsIgnoreCase(v.getStatus().toString()))
                .collect(Collectors.toList());

        for (Vehicle vehicle : activeVehicles) {
            VehicleLocation latest = locationRepo.findTopByVehicleIdOrderByReportedAtDesc(vehicle.getId())
                    .orElse(null);

            double lat, lon;
            if (latest == null) {
                lat = 41.0 + random.nextDouble() * 0.1; // 41.0 - 41.1
                lon = 29.0 + random.nextDouble() * 0.1; // 29.0 - 29.1
            } else {
                lat = latest.getLat() + (random.nextDouble() - 0.5) * 0.002;
                lon = latest.getLon() + (random.nextDouble() - 0.5) * 0.002;
            }

            VehicleLocation newLoc = new VehicleLocation();
            newLoc.setVehicleId(vehicle.getId());
            newLoc.setLat(lat);
            newLoc.setLon(lon);
            newLoc.setReportedAt(LocalDateTime.now());

            locationRepo.save(newLoc);
        }
    }
}

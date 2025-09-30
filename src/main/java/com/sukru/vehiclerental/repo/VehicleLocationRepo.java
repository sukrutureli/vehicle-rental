package com.sukru.vehiclerental.repo;

import com.sukru.vehiclerental.entity.VehicleLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VehicleLocationRepo extends JpaRepository<VehicleLocation, UUID> {

    Optional<VehicleLocation> findTopByVehicleIdOrderByReportedAtDesc(UUID vehicleId);
}

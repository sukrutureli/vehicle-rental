package com.sukru.vehiclerental.repo;

import com.sukru.vehiclerental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RentalRepo extends JpaRepository<Rental, UUID> {
	
	List<Rental> findByVehicleId(UUID vehicleId);
}

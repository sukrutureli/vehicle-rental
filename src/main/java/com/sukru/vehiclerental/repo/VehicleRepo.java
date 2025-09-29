package com.sukru.vehiclerental.repo;

import com.sukru.vehiclerental.entity.Vehicle;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepo extends JpaRepository<Vehicle, UUID> {
}

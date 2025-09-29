package com.sukru.vehiclerental.repo;

import com.sukru.vehiclerental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RentalRepo extends JpaRepository<Rental, UUID> {
}

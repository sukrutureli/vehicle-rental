package com.sukru.vehiclerental.repo;

import com.sukru.vehiclerental.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepo extends JpaRepository<Customer, UUID> {
	
	Optional<Customer> findByEmail(String email);
	
	boolean existsByEmail(String email);
}

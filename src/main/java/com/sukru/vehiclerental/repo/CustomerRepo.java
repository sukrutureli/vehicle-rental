package com.sukru.vehiclerental.repo;

import com.sukru.vehiclerental.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepo extends JpaRepository<Customer, UUID> {
}

package com.sukru.vehiclerental.controller;

import com.sukru.vehiclerental.entity.Customer;
import com.sukru.vehiclerental.repo.CustomerRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class CustomerController {

    private final CustomerRepo customerRepo;

    public CustomerController(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    @GetMapping("/customers")
    public String customersPage() {
        return "customers"; // customers.html
    }

    @GetMapping("/customers/edit")
    public String editPage() {
        return "edit-customer"; // edit-customer.html
    }

    // API

    @GetMapping("/api/customers")
    @ResponseBody
    public List<Customer> listApi() {
        return customerRepo.findAll();
    }

    @GetMapping("/api/customers/{id}")
    @ResponseBody
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID id) {
    	var opt = customerRepo.findById(id);
        if (opt.isEmpty()) {
        	throw new IllegalArgumentException("Customer not found with id: " + id);
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping("/api/customers")
    @ResponseBody
    public Customer addApi(@RequestBody Customer customer) {
        if (customerRepo.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + customer.getEmail());
        }
        return customerRepo.save(customer);
    }

    // Guncelle
    @PutMapping("/api/customers/{id}")
    @ResponseBody
    public Customer updateApi(@PathVariable UUID id, @RequestBody Customer updated) {
        var existingOpt = customerRepo.findById(id);

        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }

        var conflict = customerRepo.findByEmail(updated.getEmail())
                .filter(c -> !c.getId().equals(id));

        if (conflict.isPresent()) {
            throw new IllegalArgumentException("Email already in use by another customer.");
        }

        updated.setId(id);
        return customerRepo.save(updated);
    }

    // Sil
    @DeleteMapping("/api/customers/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteApi(@PathVariable UUID id) {
        if (!customerRepo.existsById(id)) {
            throw new IllegalArgumentException("Customer not found with id: " + id);
        }
        customerRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

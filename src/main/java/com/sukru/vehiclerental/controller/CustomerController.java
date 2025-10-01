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
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping("/api/customers")
    @ResponseBody
    public ResponseEntity<?> addApi(@RequestBody Customer customer) {
    	if (customerRepo.existsByEmail(customer.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists: " + customer.getEmail());
        }
        return ResponseEntity.ok(customerRepo.save(customer));
    }

    // Guncelle
    @PutMapping("/api/customers/{id}")
    @ResponseBody
    public ResponseEntity<?> updateApi(@PathVariable UUID id, @RequestBody Customer updated) {
        var existingOpt = customerRepo.findById(id);

        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            var conflict = customerRepo.findByEmail(updated.getEmail())
                    .filter(c -> !c.getId().equals(id));
            if (conflict.isPresent()) {
                return ResponseEntity.badRequest().body("Email already in use by another customer.");
            }

            updated.setId(id);
            Customer saved = customerRepo.save(updated);
            return ResponseEntity.ok(saved);
        }
    }

    // Sil
    @DeleteMapping("/api/customers/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteApi(@PathVariable UUID id) {
        if (!customerRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        } else {
            customerRepo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
    }
}

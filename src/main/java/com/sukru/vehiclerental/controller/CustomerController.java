package com.sukru.vehiclerental.controller;

import com.sukru.vehiclerental.entity.Customer;
import com.sukru.vehiclerental.repo.CustomerRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepo customerRepo;

    public CustomerController(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    @GetMapping
    public String customersPage() {
        return "customers"; // customers.html
    }

    @GetMapping("/edit")
    public String editPage() {
        return "edit-customer"; // edit-customer.html
    }

    // API

    @GetMapping("/api")
    @ResponseBody
    public List<Customer> listApi() {
        return customerRepo.findAll();
    }

    @PostMapping("/api")
    @ResponseBody
    public Customer addApi(@RequestBody Customer customer) {
        return customerRepo.save(customer);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Customer> updateApi(@PathVariable UUID id, @RequestBody Customer updated) {
        return customerRepo.findById(id)
                .map(c -> {
                    updated.setId(id);
                    return ResponseEntity.ok(customerRepo.save(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteApi(@PathVariable UUID id) {
        if (!customerRepo.existsById(id)) return ResponseEntity.notFound().build();
        customerRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

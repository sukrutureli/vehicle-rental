package com.sukru.vehiclerental.controller;

import com.sukru.vehiclerental.entity.Rental;
import com.sukru.vehiclerental.entity.enums.RentalStatus;
import com.sukru.vehiclerental.entity.enums.VehicleStatus;
import com.sukru.vehiclerental.repo.CustomerRepo;
import com.sukru.vehiclerental.repo.RentalRepo;
import com.sukru.vehiclerental.repo.VehicleRepo;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class RentalController {

    private final RentalRepo rentalRepo;
    private final VehicleRepo vehicleRepo;
    private final CustomerRepo customerRepo;

    public RentalController(RentalRepo rentalRepo, VehicleRepo vehicleRepo, CustomerRepo customerRepo) {
        this.rentalRepo = rentalRepo;
		this.vehicleRepo = vehicleRepo;
		this.customerRepo = customerRepo;
    }

    @GetMapping("/rentals")
    public String rentalsPage() {
        return "rentals"; // rentals.html
    }

    // API
    @GetMapping("/api/rentals")
    @ResponseBody
    public List<Rental> listApi(
            @RequestParam(required = false) String vehiclePlate,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    	
        List<Rental> rentals = rentalRepo.findAll();

        rentals.forEach(r -> {
            vehicleRepo.findById(r.getVehicleId()).ifPresent(v -> r.setVehiclePlate(v.getPlate()));
            customerRepo.findById(r.getCustomerId()).ifPresent(c -> r.setCustomerEmail(c.getEmail()));
        });

        return rentals.stream()
                .filter(r -> vehiclePlate == null || r.getVehiclePlate().equalsIgnoreCase(vehiclePlate))
                .filter(r -> customerEmail == null || r.getCustomerEmail().equalsIgnoreCase(customerEmail))
                .filter(r -> status == null || r.getStatus().name().equalsIgnoreCase(status))
                .filter(r -> startDate == null || !r.getStartDate().isBefore(startDate))
                .filter(r -> endDate == null || !r.getEndDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    
    @PostMapping("/api/rentals")
    @ResponseBody
    public Rental addApi(@RequestBody Rental rental) {
        LocalDateTime now = LocalDateTime.now();
        
        var vehicleOpt = vehicleRepo.findByPlate(rental.getVehiclePlate());
        var customerOpt = customerRepo.findByEmail(rental.getCustomerEmail());

        if (vehicleOpt.isEmpty()) {
        	throw new IllegalArgumentException("Vehicle not found with plate: " + rental.getVehiclePlate());
        }
        var vehicle = vehicleOpt.get();
        
        if (customerOpt.isEmpty()) {
        	throw new IllegalArgumentException("Customer not found with email: " + rental.getCustomerEmail());
        }
        var customer = customerOpt.get();
        
        rental.setVehicleId(vehicle.getId());
        rental.setCustomerId(customer.getId());

        if (rental.getStartDate().isBefore(vehicle.getAvailableFrom()) ||
            rental.getEndDate().isAfter(vehicle.getAvailableTo())) {
        	throw new IllegalArgumentException("Vehicle is not available in this date range.");
        }

        // Cakisma kontrolu
        List<Rental> rentals = rentalRepo.findByVehicleId(rental.getVehicleId());
        for (Rental existing : rentals) {
            if ("ACTIVE".equalsIgnoreCase(existing.getStatus().toString())) {
                boolean overlap = 
                    (rental.getStartDate().isBefore(existing.getEndDate()) &&
                     rental.getEndDate().isAfter(existing.getStartDate()));

                if (overlap) {
                	throw new IllegalArgumentException("Vehicle already rented in this date range.");
                }
            }
        }

        // Kaydet
        rental.setCreatedAt(now);
        rental.setUpdatedAt(now);
        
        vehicle.setStatus(VehicleStatus.RENTED);
        vehicle.setUpdatedAt(now);
        vehicleRepo.save(vehicle);
        
        return rentalRepo.save(rental);
    }

    
    // API - Complete
    @PutMapping("/api/rentals/{id}/complete")
    @ResponseBody
    public Rental completeRental(@PathVariable UUID id) {
    	LocalDateTime now = LocalDateTime.now();
    	
        var rentalOpt = rentalRepo.findById(id);

        if (rentalOpt.isEmpty()) {
        	throw new IllegalArgumentException("Rental not found with id: " + id);
        }

        Rental rental = rentalOpt.get();
        rental.setStatus(RentalStatus.COMPLETED);
        rental.setUpdatedAt(now);
        rentalRepo.save(rental);

        // Vehicle durumunu guncelle - AVAILABLE
        var vehicleOpt = vehicleRepo.findById(rental.getVehicleId());
        if (vehicleOpt.isPresent()) {
            var vehicle = vehicleOpt.get();
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicle.setUpdatedAt(now);
            vehicleRepo.save(vehicle);
        }

        return rental;
    }

    // API - Cancel
    @PutMapping("/api/rentals/{id}/cancel")
    @ResponseBody
    public Rental cancelRental(@PathVariable UUID id) {
    	LocalDateTime now = LocalDateTime.now();
    	
        var rentalOpt = rentalRepo.findById(id);

        if (rentalOpt.isEmpty()) {
        	throw new IllegalArgumentException("Rental not found with id: " + id);
        }

        Rental rental = rentalOpt.get();
        rental.setStatus(RentalStatus.CANCELLED);
        rental.setUpdatedAt(now);
        rentalRepo.save(rental);

        // Vehicle durumunu guncelle - AVAILABLE
        var vehicleOpt = vehicleRepo.findById(rental.getVehicleId());
        if (vehicleOpt.isPresent()) {
            var vehicle = vehicleOpt.get();
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicle.setUpdatedAt(now);
            vehicleRepo.save(vehicle);
        }

        return rental;
    }

}

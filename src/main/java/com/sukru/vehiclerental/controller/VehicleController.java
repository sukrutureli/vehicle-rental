package com.sukru.vehiclerental.controller;

import com.sukru.vehiclerental.entity.Vehicle;
import com.sukru.vehiclerental.repo.VehicleRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleRepo vehicleRepo;

    public VehicleController(VehicleRepo vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
    }

    // HTML

    // Listeleme
    @GetMapping
    public String listHtml(Model model) {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("vehicle", new Vehicle()); // form icin bos obje
        return "vehicles";
    }

    // Ekleme
    @PostMapping("/add")
    public String addVehicle(@ModelAttribute Vehicle vehicle) {
        LocalDateTime now = LocalDateTime.now();
        vehicle.setCreatedAt(now);
        vehicle.setUpdatedAt(now);
        vehicleRepo.save(vehicle);
        return "redirect:/vehicles";
    }

    // Silme
    @GetMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable UUID id) {
        if (vehicleRepo.existsById(id)) {
            vehicleRepo.deleteById(id);
        }
        return "redirect:/vehicles";
    }

    // Guncelleme sayfasi
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        return vehicleRepo.findById(id)
                .map(vehicle -> {
                    model.addAttribute("vehicle", vehicle);
                    return "edit-vehicle";
                })
                .orElse("redirect:/vehicles"); // yoksa listeye don
    }

    // Guncelleme islemi
    @PostMapping("/update")
    public String updateVehicle(@ModelAttribute Vehicle vehicle) {
        vehicleRepo.findById(vehicle.getId()).ifPresent(existing -> {
            vehicle.setCreatedAt(existing.getCreatedAt()); // eski createdAt korunur
        });
        vehicle.setUpdatedAt(LocalDateTime.now());
        vehicleRepo.save(vehicle); // aynı ID varsa update yapar
        return "redirect:/vehicles";
    }

    // API

    // Listele (JSON)
    @GetMapping("/api")
    @ResponseBody
    public List<Vehicle> listVehiclesApi() {
        return vehicleRepo.findAll();
    }

    // Ekle (JSON POST)
    @PostMapping("/api")
    @ResponseBody
    public Vehicle addVehicleApi(@RequestBody Vehicle vehicle) {
        LocalDateTime now = LocalDateTime.now();
        vehicle.setCreatedAt(now);
        vehicle.setUpdatedAt(now);
        return vehicleRepo.save(vehicle);
    }

    // Guncelle (JSON PUT)
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Vehicle> updateVehicleApi(@PathVariable("id") UUID id,
                                                    @RequestBody Vehicle updated) {
        return vehicleRepo.findById(id)
                .map(existing -> {
                    updated.setId(id); // ID sabit kalir
                    updated.setCreatedAt(existing.getCreatedAt()); // createdAt korunur
                    updated.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(vehicleRepo.save(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Sil (JSON DELETE)
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteVehicleApi(@PathVariable UUID id) {
        if (!vehicleRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        vehicleRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

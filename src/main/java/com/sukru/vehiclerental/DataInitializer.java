package com.sukru.vehiclerental;

import com.sukru.vehiclerental.entity.Customer;
import com.sukru.vehiclerental.entity.Vehicle;
import com.sukru.vehiclerental.entity.enums.FuelType;
import com.sukru.vehiclerental.entity.enums.Transmission;
import com.sukru.vehiclerental.entity.enums.VehicleStatus;
import com.sukru.vehiclerental.repo.CustomerRepo;
import com.sukru.vehiclerental.repo.VehicleRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Configuration
public class DataInitializer {

    private static final String[] BRANDS = {"Toyota", "BMW", "Ford", "Honda", "Tesla", "Renault", "Hyundai"};
    private static final String[] MODELS = {"Corolla", "X5", "Focus", "Civic", "Model 3", "Clio", "Getz"};
    private static final String[] CITIES = {"Istanbul", "Ankara", "Izmir", "Bursa", "Antalya", "Amasya"};
    private static final String[] FIRST_NAMES = {"Ahmet", "Mehmet", "Erhan", "Fatma", "Ali", "Zeynep", "Arda"};
    private static final String[] LAST_NAMES = {"Yılmaz", "Demir", "Toprak", "Gümüş", "Bulut", "Kalın"};

    @Bean
    CommandLineRunner initDatabase(CustomerRepo customerRepo, VehicleRepo vehicleRepo) {
        return args -> {
            Random random = new Random();
            int randomNumber = 0;

            // Sadece bossa doldur
            if (customerRepo.count() == 0) {
                for (int i = 0; i < 10; i++) {
                    String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
                    String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
                    String email = "user" + i + "@mail.com";
                    String phone = "05";
                    for (int j = 0; j < 9; j++) {
                        phone += random.nextInt(10);
                    }

                    Customer customer = new Customer();
                    customer.setFirstName(firstName);
                    customer.setLastName(lastName);
                    customer.setEmail(email);
                    customer.setPhone(phone);
                    customerRepo.save(customer);
                }
                System.out.println("10 müşteri eklendi");
            }

            if (vehicleRepo.count() == 0) {
                for (int i = 0; i < 15; i++) {
                    Vehicle v = new Vehicle();
                    randomNumber = random.nextInt(BRANDS.length);
                    v.setBrand(BRANDS[randomNumber]);
                    v.setModel(MODELS[randomNumber]);
                    v.setCity(CITIES[random.nextInt(CITIES.length)]);
                    v.setPlate(generateRandomPlate(random));
                    v.setDailyPrice(200 + random.nextInt(500));
                    v.setFuelType(FuelType.values()[random.nextInt(FuelType.values().length)]);
                    v.setTransmission(Transmission.values()[random.nextInt(Transmission.values().length)]);
                    v.setStatus(VehicleStatus.AVAILABLE);

                    LocalDateTime now = LocalDateTime.now();
                    v.setAvailableFrom(now.plusDays(random.nextInt(4)).truncatedTo(ChronoUnit.MINUTES));
                    v.setAvailableTo(now.plusDays(4 + random.nextInt(24)).truncatedTo(ChronoUnit.MINUTES));
                    v.setCreatedAt(now.truncatedTo(ChronoUnit.MINUTES));
                    v.setUpdatedAt(now.truncatedTo(ChronoUnit.MINUTES));

                    vehicleRepo.save(v);
                }
                System.out.println("15 araç eklendi");
            }
        };
    }
    
    private String generateRandomPlate(Random random) {
        
        int cityCode = 1 + random.nextInt(81);
        String cityPart = String.format("%02d", cityCode);

        StringBuilder letters = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            char c = (char) ('A' + random.nextInt(26));
            letters.append(c);
        }

        int digits = 1 + random.nextInt(1000);
        String digitPart = String.format("%03d", digits);

        return cityPart + letters + digitPart;
    }

}

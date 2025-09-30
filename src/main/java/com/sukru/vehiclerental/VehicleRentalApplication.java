package com.sukru.vehiclerental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VehicleRentalApplication {

	public static void main(String[] args) {
		SpringApplication.run(VehicleRentalApplication.class, args);
	}

}

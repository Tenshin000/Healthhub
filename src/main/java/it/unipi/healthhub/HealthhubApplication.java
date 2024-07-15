package it.unipi.healthhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController

public class HealthhubApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthhubApplication.class, args);
	}

}

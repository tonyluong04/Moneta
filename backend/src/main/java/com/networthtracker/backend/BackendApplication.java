package com.networthtracker.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// the app entry point — starts the whole Spring Boot backend.
@SpringBootApplication
@EnableScheduling // app-wide; each job decides for itself whether it runs (e.g. moneta.fx.enabled)
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}

package com.picanounon.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PicanounonBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(PicanounonBackApplication.class, args);
	}

}

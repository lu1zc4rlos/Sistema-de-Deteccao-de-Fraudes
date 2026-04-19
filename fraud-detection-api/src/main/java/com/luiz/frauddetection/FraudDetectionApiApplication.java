package com.luiz.frauddetection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FraudDetectionApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FraudDetectionApiApplication.class, args);
	}

}

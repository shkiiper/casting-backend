package com.casting.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CastingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(CastingPlatformApplication.class, args);
	}

}
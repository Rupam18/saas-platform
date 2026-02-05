package com.rupam.saas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SaasPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaasPlatformApplication.class, args);
	}

}

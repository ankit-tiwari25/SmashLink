package com.project.smashlink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SmashlinkApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmashlinkApplication.class, args);
	}

}

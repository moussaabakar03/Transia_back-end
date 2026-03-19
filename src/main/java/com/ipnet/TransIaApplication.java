package com.ipnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@EnableJpaAuditing
@SpringBootApplication
public class TransIaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransIaApplication.class, args);
	}

}

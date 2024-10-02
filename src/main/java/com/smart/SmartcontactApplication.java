package com.smart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication			// It enables @configuration, @EnableAutoConfiguration and @ComponentScan tags
public class SmartcontactApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartcontactApplication.class, args);
	}

}

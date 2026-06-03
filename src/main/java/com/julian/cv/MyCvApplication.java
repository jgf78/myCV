package com.julian.cv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MyCvApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyCvApplication.class, args);
	}

}

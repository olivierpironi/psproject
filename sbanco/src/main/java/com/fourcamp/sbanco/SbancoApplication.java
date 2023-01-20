package com.fourcamp.sbanco;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SbancoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SbancoApplication.class, args);
	}
 
	@Override
	public void run(String... args) throws Exception {
	}

}

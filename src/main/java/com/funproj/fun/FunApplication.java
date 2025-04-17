package com.funproj.fun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class FunApplication {
	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = "password";
		String hashedPassword = encoder.encode(rawPassword);
		System.out.println("Hashed password: " + hashedPassword);
		SpringApplication.run(FunApplication.class, args);
	}
}
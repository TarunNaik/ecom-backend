package com.example.ecommerce;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;

@SpringBootApplication
public class EcommerceApplication {

	public static void main(String[] args) {

        SpringApplication.run(EcommerceApplication.class, args);
	}

}
package com.example.ecommerce;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Base64;

@SpringBootApplication
public class EcommerceApplication {

	public static void main(String[] args) {

        SpringApplication.run(EcommerceApplication.class, args);
//        byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded();
//        System.out.println(Base64.getEncoder().encodeToString(key));
	}

}
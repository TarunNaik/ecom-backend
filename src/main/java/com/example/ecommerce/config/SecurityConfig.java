package com.example.ecommerce.config;

import com.example.ecommerce.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors().and().csrf().disable()
            .headers().frameOptions().disable() // Allow H2 console to be displayed in frames
            .and()
            .userDetailsService(userDetailsService) // Configure UserDetailsService
            .authorizeRequests()
            .antMatchers("/h2-console/**").permitAll() // Allow H2 console access
            .antMatchers("/api/auth/**", "/api/products/**", "/vendor/products/**").permitAll() // Allow public access to auth and product list
            .antMatchers("/api/products/**").hasAuthority("ROLE_VENDOR") // Vendor specific endpoints
//            .antMatchers("/api/admin/**").hasAuthority("ADMIN") // Admin specific endpoints
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Use stateless sessions for REST API
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
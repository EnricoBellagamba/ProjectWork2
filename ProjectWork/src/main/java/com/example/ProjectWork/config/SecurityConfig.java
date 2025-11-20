package com.example.ProjectWork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    // Permessi di accesso a diversi endpoint in base al ruolo
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // le API di autenticazione devono essere pubbliche
                        .requestMatchers("/api/auth/**").permitAll()
                        // i cv devono essere scaricabili da tutti
                        .requestMatchers("/uploads/**").permitAll()
                        // le API sugli utenti richiedono login
                        .requestMatchers("/api/utenti/**").authenticated()
                        // tutto il resto per ora lo lasciamo aperto
                        .anyRequest().permitAll()
                )
                .httpBasic(withDefaults());

        return http.build();
    }


    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }


    // Definizione PasswordEncoder per criptare le password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

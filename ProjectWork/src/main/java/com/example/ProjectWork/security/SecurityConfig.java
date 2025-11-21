package com.example.ProjectWork.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // ENDPOINT PUBBLICI
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/", "/error", "/favicon.ico").permitAll()

                        // SOLO HR
                        .requestMatchers("/api/hr/**").hasRole("HR")

                        // SOLO CANDIDATO
                        .requestMatchers("/api/candidati/**").hasRole("CANDIDATO")

                        // QUALSIASI COSA NON MATCHATA PRIMA → RICHIEDE LOGIN
                        .anyRequest().authenticated()
                )

                // Aggiungiamo il filtro JWT PRIMA del filtro UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Necessario se un domani usi AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

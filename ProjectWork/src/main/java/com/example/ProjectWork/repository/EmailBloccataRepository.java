package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.EmailBloccata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailBloccataRepository extends JpaRepository<EmailBloccata, String> {
    Optional<EmailBloccata> findByEmail(String email);
}
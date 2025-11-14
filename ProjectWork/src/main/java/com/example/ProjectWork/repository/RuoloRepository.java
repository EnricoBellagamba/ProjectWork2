package com.example.ProjectWork.repository;

import  com.example.ProjectWork.model.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RuoloRepository extends JpaRepository<Ruolo, Long> {

    Optional<Ruolo> findByCodice(String codice);
}
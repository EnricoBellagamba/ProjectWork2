package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.StatoCandidatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatoCandidaturaRepository extends JpaRepository<StatoCandidatura, Long> {

    /**
     * Recupera lo stato candidatura tramite codice (IN_VALUTAZIONE, RESPINTA, ACCETTATA, ...).
     */
    Optional<StatoCandidatura> findByCodice(String codice);
}

package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.StatoPosizione;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatoPosizioneRepository extends JpaRepository<StatoPosizione, Long> {
    Optional<StatoPosizione> findByCodice(String codice);
}

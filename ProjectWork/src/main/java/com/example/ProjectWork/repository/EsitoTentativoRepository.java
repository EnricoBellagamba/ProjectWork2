package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.EsitoTentativo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EsitoTentativoRepository extends JpaRepository<EsitoTentativo, Long> {

    /**
     * Cerca un esito tentativo tramite codice:
     * - SUPERATO
     * - NON_SUPERATO
     * - IN_VALUTAZIONE
     */
    Optional<EsitoTentativo> findByCodice(String codice);
}

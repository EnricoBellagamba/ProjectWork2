package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.EsitoTentativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EsitoTentativoRepository extends JpaRepository<EsitoTentativo, Long> {
    Optional<EsitoTentativo> findByCodice(String codice);
}

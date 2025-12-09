package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

    boolean existsByCandidato_IdCandidatoAndPosizione_IdPosizione(Long idCandidato, Long idPosizione);

    Optional<Candidatura> findByCandidato_IdCandidatoAndPosizione_IdPosizione(Long idCandidato, Long idPosizione);

    List<Candidatura> findByCandidato_IdUtente_IdUtente(Long idUtente);

    List<Candidatura> findByPosizione_IdPosizione(Long idPosizione);

    long countByPosizione_CreatedByHR(com.example.ProjectWork.model.Utente createdByHR);
}

package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Candidatura;
import com.example.ProjectWork.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

    // Tutte le candidature per posizione
    List<Candidatura> findByPosizione_IdPosizione(Long idPosizione);

    // Tutte le candidature di un utente (via candidato → idUtente)
    List<Candidatura> findByCandidato_IdUtente(Utente utente);

    // Verifica esistenza candidatura candidato + posizione
    boolean existsByCandidato_IdCandidatoAndPosizione_IdPosizione(Long idCandidato, Long idPosizione);
}

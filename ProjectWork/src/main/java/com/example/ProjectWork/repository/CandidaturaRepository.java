package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

    // Controllo esistenza candidatura per coppia candidato/posizione
    boolean existsByCandidato_IdCandidatoAndPosizione_IdPosizione(Long idCandidato, Long idPosizione);

    // Recupera candidatura specifica per candidato e posizione
    Optional<Candidatura> findByCandidato_IdCandidatoAndPosizione_IdPosizione(Long idCandidato, Long idPosizione);

    // Tutte le candidature di un singolo utente → necessario per parte candidato
    List<Candidatura> findByCandidato_IdUtente_IdUtente(Long idUtente);

    // Tutte le candidature legate a una posizione → usato lato HR
    List<Candidatura> findByPosizione_IdPosizione(Long idPosizione);

    /**
     * Conta le candidature relative a posizioni create da un certo HR.
     * (naviga la relazione posizione.createdByHR)
     */
    long countByPosizione_CreatedByHR(com.example.ProjectWork.model.Utente createdByHR);
}

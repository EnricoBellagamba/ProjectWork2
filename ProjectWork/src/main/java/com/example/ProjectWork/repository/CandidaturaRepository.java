package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Candidatura;
import com.example.ProjectWork.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {
    boolean existsByCandidato_IdCandidatoAndPosizione_IdPosizione(Long idCandidato, Long idPosizione);

    List<Candidatura> findByCandidato_IdUtente(Utente idUtente);
}

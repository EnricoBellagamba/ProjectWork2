package com.example.ProjectWork.repository;

import com.example.ProjectWork.dto.CandidatoConPosizioneDTO;
import com.example.ProjectWork.model.Candidato;
import com.example.ProjectWork.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    // Query native per ritornare una lista di candidati adatti a una posizione in base al parametro passato, case insensitive
    @Query(value = """
        SELECT 
            c.idCandidato AS idCandidato,
            c.idUtente AS idUtente,
            c.isActive AS isActive,
            u.nome AS nome,
            u.cognome AS cognome,
            u.email AS email,
            u.telefono AS telefono,
            u.lastLogin AS lastLogin,
            u.citta AS citta,
            u.lingua AS lingua,
            u.cvUrl AS cvUrl,
            p.titolo AS posizioneTitolo
        FROM dbo.CANDIDATO c
        JOIN dbo.CANDIDATURA ca ON ca.idCandidato = c.idCandidato
        JOIN dbo.POSIZIONE p ON p.idPosizione = ca.idPosizione
        JOIN dbo.UTENTE u ON u.idUtente = c.idUtente
        WHERE p.titolo COLLATE SQL_Latin1_General_CP1_CI_AS LIKE CONCAT('%', :titolo, '%')
        ORDER BY c.isActive DESC
    """, nativeQuery = true)
    List<CandidatoConPosizioneDTO> findAllByPosizioneDto(@Param("titolo") String titolo);
    Optional<Candidato> findByIdUtente(Utente idUtente);




}

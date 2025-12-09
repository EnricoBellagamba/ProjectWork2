package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Opzione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpzioneRepository extends JpaRepository<Opzione, Long> {

    /**
     * Restituisce tutte le opzioni appartenenti a una certa domanda.
     * Usa la relazione ManyToOne:
     *   Opzione.domanda.idDomanda
     */
    List<Opzione> findByDomanda_IdDomanda(Long idDomanda);
}

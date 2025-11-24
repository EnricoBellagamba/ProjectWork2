package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosizioneRepository extends JpaRepository<Posizione, Long> {

    /**
     * Ritorna tutte le posizioni create da un certo HR.
     * Usato dall'endpoint GET /api/posizioni/hr/mie
     */
    List<Posizione> findByCreatedByHR(Utente createdByHR);

}

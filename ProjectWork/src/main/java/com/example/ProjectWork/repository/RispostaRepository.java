package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Domanda;
import com.example.ProjectWork.model.Risposta;
import com.example.ProjectWork.model.TentativoTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RispostaRepository extends JpaRepository<Risposta, Long> {

    /**
     * Tutte le risposte associate a un tentativo (per ID del tentativo).
     */
    List<Risposta> findByIdTentativo_IdTentativo(Long idTentativo);

    /**
     * Tutte le risposte associate a un tentativo (entità TentativoTest).
     */
    List<Risposta> findByIdTentativo(TentativoTest tentativo);

    /**
     * Cerca una risposta per una certa domanda *all'interno di un tentativo*.
     *
     * È fondamentale perché nel nuovo controller vogliamo:
     * - aggiornare una risposta esistente
     * - oppure crearla se ancora non esiste
     *
     * Il DB garantisce l’unicità tramite la UNIQUE(idTentativo, idDomanda),
     * quindi questo metodo riflette esattamente il vincolo.
     */
    Optional<Risposta> findByIdTentativoAndIdDomanda(TentativoTest tentativo, Domanda domanda);

    /**
     * Tutte le risposte rispetto a una singola domanda.
     * Utile per statistiche o debug.
     */
    List<Risposta> findByIdDomanda_IdDomanda(Long idDomanda);
}

package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Risposta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RispostaRepository extends JpaRepository<Risposta, Long> {

    /**
     * Tutte le risposte associate a un certo tentativo.
     * Verrà usato per calcolare il riepilogo (numero risposte, corrette, ecc.).
     */
    List<Risposta> findByIdTentativo_IdTentativo(Long idTentativo);
}

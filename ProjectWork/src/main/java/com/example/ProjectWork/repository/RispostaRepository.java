package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Risposta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RispostaRepository extends JpaRepository<Risposta, Long> {

    List<Risposta> findByIdTentativo_IdTentativo(Long idTentativo);
}

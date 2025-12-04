package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.TentativoTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TentativoTestRepository extends JpaRepository<TentativoTest, Long> {

    // lista completa dei tentativi per una candidatura
    List<TentativoTest> findAllByIdCandidatura(Long idCandidatura);

    // singolo tentativo (se richiesto in altri punti)
    TentativoTest findByIdCandidatura(Long idCandidatura);
}

package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Domanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomandaRepository extends JpaRepository<Domanda, Long> {

    List<Domanda> findByTest_IdTest(Long idTest);

    int countByTest_IdTest(Long idTest);
}

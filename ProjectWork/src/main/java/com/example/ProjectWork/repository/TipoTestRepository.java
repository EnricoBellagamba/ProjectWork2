package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.TipoTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoTestRepository extends JpaRepository<TipoTest, Long> {

    Optional<TipoTest> findByCodice(String codice);
}

package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.TentativoTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TentativoTestRepository extends JpaRepository<TentativoTest, Long> {

    List<TentativoTest> findAllByIdCandidatura(Long idCandidatura);

}

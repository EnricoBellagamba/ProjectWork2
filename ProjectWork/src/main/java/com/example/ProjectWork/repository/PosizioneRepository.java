package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Posizione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PosizioneRepository extends JpaRepository<Posizione, Long> {

    @Query(value = """
            SELECT TOP 4 *
            FROM dbo.POSIZIONE
            ORDER BY pubblicataAt DESC;
        """, nativeQuery = true)
    public List<Posizione> topQuattroPosizioni();

}

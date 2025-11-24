package com.example.ProjectWork.repository;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.Utente;
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

    //tutte le posizioni create da un certo HR
    List<Posizione> findByCreatedByHR(Utente hr);

}

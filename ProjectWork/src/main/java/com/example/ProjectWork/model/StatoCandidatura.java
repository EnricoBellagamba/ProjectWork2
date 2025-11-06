package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dbo.STATO_CANDIDATURA")
public class StatoCandidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStatoCandidatura;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column
    private String descrizione;

    @Column(nullable = false)
    private Boolean isFinale;
}

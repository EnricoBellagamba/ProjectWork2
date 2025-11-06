package com.example.ProjectWork.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
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

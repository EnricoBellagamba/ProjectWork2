package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
public class StatoCandidatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStatoCandidatura;
    private String codice;
    private String descrizione;
}

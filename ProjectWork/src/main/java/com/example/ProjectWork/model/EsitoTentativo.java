package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
public class EsitoTentativo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEsitoTentativo;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column
    private String descrizione;
}

package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
public class EsitoTentativo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEsitoTentativo;
    private String codice;
    private String descrizione;
}

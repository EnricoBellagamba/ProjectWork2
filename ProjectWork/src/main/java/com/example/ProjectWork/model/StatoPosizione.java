package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
public class StatoPosizione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStatoPosizione;
    private String codice;
    private String descrizione;
}
package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "STATO_POSIZIONE", schema = "dbo")
public class StatoPosizione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStatoPosizione;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column
    private String descrizione;
}
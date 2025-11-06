package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dbo.STATO_POSIZIONE")
public class StatoPosizione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStatoPosizione;

    @Column(nullable = false, unique = true)
    private Integer codice;

    @Column
    private String descrizione;
}
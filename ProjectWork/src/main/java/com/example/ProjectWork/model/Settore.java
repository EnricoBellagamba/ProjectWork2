package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "SETTORE", schema = "dbo")
public class Settore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSettore;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column(nullable = false)
    private String nome;

    @Column
    private String descrizione;

}

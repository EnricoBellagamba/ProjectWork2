package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dbo.TIPO_TEST")
public class TipoTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoTest;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column
    private String descrizione;
}

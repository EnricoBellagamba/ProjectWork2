package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
public class TipoTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoTest;
    private String codice;
    private String descrizione;
}

package com.example.ProjectWork.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Ruolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRuolo;

    @Column(nullable = false)
    private String codice;

    private String descrizione;

    @OneToMany(mappedBy = "ruolo")
    private List<Utente> utenti;
}

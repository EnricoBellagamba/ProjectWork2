package com.example.ProjectWork.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Ruolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRuolo;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column(nullable = false)
    private String descrizione;

    @OneToMany(mappedBy = "idRuolo")
    private List<Utente> utenti;

}

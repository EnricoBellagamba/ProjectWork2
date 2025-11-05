package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUtente;

    private String email;
    private String passwordHash;
    private String nome;
    private String cognome;
    private String telefono;
    private String lingua;

    @ManyToOne
    @JoinColumn(name = "idRuolo")
    private Ruolo ruolo;

    // Getters e setters
}
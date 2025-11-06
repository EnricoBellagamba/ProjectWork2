package com.example.ProjectWork.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "dbo.UTENTE")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUtente;

    @Column(nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cognome;

    @Column(nullable = false)
    private Instant lastLogin; //memorizza un punto preciso nel tempo in UTC. Ottimo se vuoi uniformità e usi microservizi / più server.

    @Column(nullable = false)
    private LocalDate dataNascita;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String citta;

    @Column(nullable = false)
    private String lingua;

    @Column(nullable = false)
    private Boolean consensoPrivacy;

    @Column(nullable = false)
    private String cvUrl;

    @Column(nullable = false)
    private String cvHash;

    @ManyToOne
    @JoinColumn(name = "idRuolo")
    private Ruolo idRuolo;

}
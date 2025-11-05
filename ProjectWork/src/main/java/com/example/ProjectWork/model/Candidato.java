package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCandidato;

    @OneToOne
    @JoinColumn(name = "idUtente")
    private Utente utente;
}
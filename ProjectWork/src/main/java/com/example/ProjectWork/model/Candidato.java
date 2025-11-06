package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCandidato;

    @OneToOne
    @JoinColumn(name = "idUtente")
    @Column(nullable = false)
    private Utente idUtente;

    @Column(nullable = false)
    private Boolean isActive;


}
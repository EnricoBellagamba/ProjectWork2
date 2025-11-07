package com.example.ProjectWork.model;

import jakarta.persistence.*;


@Entity
@Table(name = "CANDIDATO", schema = "dbo")
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCandidato;

    @OneToOne
    @JoinColumn(name = "idUtente")
    private Utente idUtente;

    @Column(nullable = false)
    private Boolean isActive;


}
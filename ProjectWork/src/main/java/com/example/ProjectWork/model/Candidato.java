package com.example.ProjectWork.model;

import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
@Table(name = "CANDIDATO", schema = "dbo")
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCandidato;

    @OneToOne
    @JoinColumn(name = "idUtente")
    private Utente idUtente;

    @Column(nullable = false)
    private Boolean isActive;


}
package com.example.ProjectWork.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCandidatura;

    @ManyToOne
    @JoinColumn(name = "idCandidato")
    private Candidato candidato;

    @ManyToOne
    @JoinColumn(name = "idPosizione")
    private Posizione posizione;

//    @ManyToOne
//    @JoinColumn(name = "idStatoCandidatura")
//    private StatoCandidatura stato;

    @Column(nullable = false)
    private LocalDate dataInvio;
}

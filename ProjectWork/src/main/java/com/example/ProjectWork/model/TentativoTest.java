package com.example.ProjectWork.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class TentativoTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTentativo;

    @ManyToOne
    @JoinColumn(name = "idCandidatura")
    private Candidatura candidatura;

    @ManyToOne
    @JoinColumn(name = "idTest")
    private Test test;

    @ManyToOne
    @JoinColumn(name = "idEsitoTentativo")
    private EsitoTentativo esito;

    private LocalDate inizio;
    private LocalDate fine;

    @OneToMany(mappedBy = "tentativo")
    private List<Risposta> risposte;
}

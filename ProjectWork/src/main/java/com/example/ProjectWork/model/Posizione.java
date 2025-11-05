package com.example.ProjectWork.model;


import jakarta.persistence.*;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Posizione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPosizione;

    private String titolo;
    private String descrizione;
    private String sede;
    private String contratto;
    private Double ral;
    private LocalDate pubblicataAl;
    private LocalDate chiusaAl;
    private Boolean chiusa;

    @ManyToOne
    @JoinColumn(name = "idStatoPosizione")
    private StatoPosizione statoPosizione;

    @OneToMany(mappedBy = "posizione")
    private List<Candidatura> candidature;

//    @ManyToMany
//    @JoinTable(
//            name = "POSIZIONE_TEST",
//            joinColumns = @JoinColumn(name = "idPosizione"),
//            inverseJoinColumns = @JoinColumn(name = "idTest")
//    )
//    private List<Test> testAssociati;
}


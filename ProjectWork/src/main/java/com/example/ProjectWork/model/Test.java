package com.example.ProjectWork.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTest;

    private String titolo;
    private String descrizione;
    private Integer durataMinuti;
    private Integer numeroDomande;
    private Double punteggioMax;

    @ManyToOne
    @JoinColumn(name = "idTipoTest")
    private TipoTest tipoTest;

    @OneToMany(mappedBy = "test")
    private List<Domanda> domande;

    @ManyToMany(mappedBy = "testAssociati")
    private List<Posizione> posizioni;

    @ManyToMany
    @JoinTable(
            name = "SETTORE_TEST",
            joinColumns = @JoinColumn(name = "idTest"),
            inverseJoinColumns = @JoinColumn(name = "idSettore")
    )
    private List<Settore> settori;
}

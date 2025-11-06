package com.example.ProjectWork.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTest;

    @Column(nullable = false)
    private String titolo;

    @Column
    private String descrizione;

    @Column(nullable = false)
    private Integer durataMinuti;

    @Column(nullable = false)
    private Integer numeroDomande;

    @Column(nullable = false)
    private Double punteggioMax;

    @Column(nullable = false)
    private Double punteggioMin;

    @Column(nullable = false)
    private Boolean isAttivo;

    @ManyToOne
    @JoinColumn(name = "idTipoTest")
    private TipoTest tipoTest;

}

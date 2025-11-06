package com.example.ProjectWork.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Domanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDomanda;

    @ManyToOne
    @JoinColumn(name = "idTest")
    private Test test;

    @Column(nullable = false)
    private String testo;

}

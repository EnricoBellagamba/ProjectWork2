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

    private String testo;

    @OneToMany(mappedBy = "domanda")
    private List<Opzione> opzioni;
}

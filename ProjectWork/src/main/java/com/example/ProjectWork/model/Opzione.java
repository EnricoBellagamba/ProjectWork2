package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
public class Opzione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOpzione;

    private String testoOpzione;
    private Boolean corretta;

    @ManyToOne
    @JoinColumn(name = "idDomanda")
    private Domanda domanda;
}

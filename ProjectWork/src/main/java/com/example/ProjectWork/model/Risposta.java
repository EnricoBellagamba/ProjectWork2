package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
public class Risposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRisposta;

    @ManyToOne
    @JoinColumn(name = "idTentativo")
    private TentativoTest tentativo;

    @ManyToOne
    @JoinColumn(name = "idDomanda")
    private Domanda domanda;

    @ManyToOne
    @JoinColumn(name = "idOpzione")
    private Opzione scelta;
}

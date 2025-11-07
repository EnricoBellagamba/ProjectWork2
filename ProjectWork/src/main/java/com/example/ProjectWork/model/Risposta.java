package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "RISPOSTA", schema = "dbo")
public class Risposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRisposta;

    @Column
    private Integer punteggioAssegnato;

    @ManyToOne
    @JoinColumn(name = "idTentativo")
    private TentativoTest idTentativo;

    @ManyToOne
    @JoinColumn(name = "idDomanda")
    private Domanda idDomanda;

    @ManyToOne
    @JoinColumn(name = "scelta")
    private Opzione idOpzione; //scelta
}

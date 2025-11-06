package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dbo.OPZIONE")
public class Opzione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOpzione;

    @ManyToOne
    @JoinColumn(name = "idDomanda")
    private Domanda idDomanda;

    @Column(nullable = false)
    private String testoOpzione;

    @Column(nullable = false)
    private Boolean iscorretta;

}

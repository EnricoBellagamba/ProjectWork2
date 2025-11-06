package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "dbo.DOMANDA")
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

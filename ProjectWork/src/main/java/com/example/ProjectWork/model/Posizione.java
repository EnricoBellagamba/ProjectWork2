package com.example.ProjectWork.model;


import jakarta.persistence.*;
import java.time.LocalDate;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "POSIZIONE", schema = "dbo")
public class Posizione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPosizione;

    @Column(nullable = false)
    private String titolo;

    @Column
    private String descrizione;

    @Column
    private String sede;

    @Column
    private String contratto;

    @Column
    private Double RAL;

    @Column(nullable = false)
    @Min(value=0)
    private Long candidatureRicevute;

    @Column
    private LocalDate pubblicataAt;

    @Column
    private LocalDate chiusaAt;

    @ManyToOne
    @JoinColumn(name = "createdByHR")
    private Utente createdByHR;

    @ManyToOne
    @JoinColumn(name = "idStatoPosizione")
    private StatoPosizione idStatoPosizione;

    @ManyToOne
    @JoinColumn(name = "idSettore")
    private Settore idSettore;

}


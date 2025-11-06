package com.example.ProjectWork.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class TentativoTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTentativo;

    @ManyToOne
    @JoinColumn(name = "idCandidatura")
    private Candidatura idCandidatura;

    @ManyToOne
    @JoinColumn(name = "idTest")
    private Test idTest;

    @OneToOne
    @JoinColumn(name = "idEsitoTentativo")
    private EsitoTentativo idEsitoTentativo;

    @Column(nullable = false)
    private LocalDate IniziatoAt;

    @Column
    private LocalDate completatoAt;

    @Column(nullable = false)
    private Integer punteggioTotale;

}

package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
public class Settore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSettore;
    private String codice;
    private String nome;
}

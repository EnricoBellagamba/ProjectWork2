package com.example.ProjectWork.dto.test;

import jakarta.validation.constraints.Max;

import java.util.List;

public class DomandaDto {
    private Long idDomanda;
    private String testo;

    @Max(value = 4, message = "Opzioni massime per domanda 4")
    private List<OpzioneDto> opzioni;

    public DomandaDto(Long idDomanda, String testo, List<OpzioneDto> opzioni) {
        this.idDomanda = idDomanda;
        this.testo = testo;
        this.opzioni = opzioni;
    }

    public Long getIdDomanda() {
        return idDomanda;
    }

    public String getTesto() {
        return testo;
    }

    public List<OpzioneDto> getOpzioni() {
        return opzioni;
    }
}

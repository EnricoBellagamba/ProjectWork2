package com.example.ProjectWork.dto.test;

public class OpzioneDto {
    private Long idOpzione;
    private String testoOpzione;

    public OpzioneDto(Long idOpzione, String testoOpzione) {
        this.idOpzione = idOpzione;
        this.testoOpzione = testoOpzione;
    }

    public Long getIdOpzione() {
        return idOpzione;
    }

    public String getTestoOpzione() {
        return testoOpzione;
    }
}

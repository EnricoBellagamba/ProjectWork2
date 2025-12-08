package com.example.ProjectWork.dto.test;

public class OpzioneDto {

    private Long idOpzione;
    private String testoOpzione;
    private Boolean corretta;

    public OpzioneDto() {
    }

    public OpzioneDto(Long idOpzione, String testoOpzione) {
        this.idOpzione = idOpzione;
        this.testoOpzione = testoOpzione;
        this.corretta = null;
    }

    public OpzioneDto(Long idOpzione, String testoOpzione, Boolean corretta) {
        this.idOpzione = idOpzione;
        this.testoOpzione = testoOpzione;
        this.corretta = corretta;
    }

    public Long getIdOpzione() {
        return idOpzione;
    }

    public void setIdOpzione(Long idOpzione) {
        this.idOpzione = idOpzione;
    }

    public String getTestoOpzione() {
        return testoOpzione;
    }

    public void setTestoOpzione(String testoOpzione) {
        this.testoOpzione = testoOpzione;
    }

    public Boolean getCorretta() {
        return corretta;
    }

    public void setCorretta(Boolean corretta) {
        this.corretta = corretta;
    }
}

package com.example.ProjectWork.dto.candidatura;

public class CandidaturaMiaDto {

    private Long idCandidatura;
    private PosizioneDto posizione;
    private String createdAT;
    private StatoDto stato;
    private Integer punteggioTest;
    private Integer numeroDomande;

    public CandidaturaMiaDto() {
    }

    public CandidaturaMiaDto(Long idCandidatura, PosizioneDto posizione, String createdAT, StatoDto stato, Integer punteggioTest, Integer numeroDomande) {
        this.idCandidatura = idCandidatura;
        this.posizione = posizione;
        this.createdAT = createdAT;
        this.stato = stato;
        this.punteggioTest = punteggioTest;
        this.numeroDomande = numeroDomande;
    }

    public Integer getNumeroDomande() {
        return numeroDomande;
    }

    public void setNumeroDomande(Integer numeroDomande) {
        this.numeroDomande = numeroDomande;
    }

    public Long getIdCandidatura() {
        return idCandidatura;
    }

    public void setIdCandidatura(Long idCandidatura) {
        this.idCandidatura = idCandidatura;
    }

    public PosizioneDto getPosizione() {
        return posizione;
    }

    public void setPosizione(PosizioneDto posizione) {
        this.posizione = posizione;
    }

    public String getCreatedAT() {
        return createdAT;
    }

    public void setCreatedAT(String createdAT) {
        this.createdAT = createdAT;
    }

    public StatoDto getStato() {
        return stato;
    }

    public void setStato(StatoDto stato) {
        this.stato = stato;
    }

    public Integer getPunteggioTest() {
        return punteggioTest;
    }

    public void setPunteggioTest(Integer punteggioTest) {
        this.punteggioTest = punteggioTest;
    }

    // DTO annidato per la posizione
    public static class PosizioneDto {
        private Long idPosizione;
        private String titolo;
        private String sede;
        private String contratto;

        public PosizioneDto() {
        }

        public PosizioneDto(Long idPosizione, String titolo, String sede, String contratto) {
            this.idPosizione = idPosizione;
            this.titolo = titolo;
            this.sede = sede;
            this.contratto = contratto;
        }

        public Long getIdPosizione() {
            return idPosizione;
        }

        public void setIdPosizione(Long idPosizione) {
            this.idPosizione = idPosizione;
        }

        public String getTitolo() {
            return titolo;
        }

        public void setTitolo(String titolo) {
            this.titolo = titolo;
        }

        public String getSede() {
            return sede;
        }

        public void setSede(String sede) {
            this.sede = sede;
        }

        public String getContratto() {
            return contratto;
        }

        public void setContratto(String contratto) {
            this.contratto = contratto;
        }
    }

    // DTO annidato per lo stato candidatura
    public static class StatoDto {
        private String codice;
        private String descrizione;

        public StatoDto() {
        }

        public StatoDto(String codice, String descrizione) {
            this.codice = codice;
            this.descrizione = descrizione;
        }

        public String getCodice() {
            return codice;
        }

        public void setCodice(String codice) {
            this.codice = codice;
        }

        public String getDescrizione() {
            return descrizione;
        }

        public void setDescrizione(String descrizione) {
            this.descrizione = descrizione;
        }
    }
}

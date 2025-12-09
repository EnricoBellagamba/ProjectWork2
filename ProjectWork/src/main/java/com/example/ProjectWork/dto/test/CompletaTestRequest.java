package com.example.ProjectWork.dto.test;

import java.time.LocalDateTime;
import java.util.List;

public class CompletaTestRequest {

    private Long idTest;
    private Long idPosizione;
    private LocalDateTime iniziatoAt;
    private List<RispostaInput> risposte;

    public static class RispostaInput {
        private Long idDomanda;
        private Long idOpzione;

        public RispostaInput() {}

        public Long getIdDomanda() {
            return idDomanda;
        }

        public void setIdDomanda(Long idDomanda) {
            this.idDomanda = idDomanda;
        }

        public Long getIdOpzione() {
            return idOpzione;
        }

        public void setIdOpzione(Long idOpzione) {
            this.idOpzione = idOpzione;
        }
    }

    public CompletaTestRequest() {}

    // Getters e Setters
    public Long getIdTest() {
        return idTest;
    }

    public void setIdTest(Long idTest) {
        this.idTest = idTest;
    }

    public Long getIdPosizione() {
        return idPosizione;
    }

    public void setIdPosizione(Long idPosizione) {
        this.idPosizione = idPosizione;
    }

    public LocalDateTime getIniziatoAt() {
        return iniziatoAt;
    }

    public void setIniziatoAt(LocalDateTime iniziatoAt) {
        this.iniziatoAt = iniziatoAt;
    }

    public List<RispostaInput> getRisposte() {
        return risposte;
    }

    public void setRisposte(List<RispostaInput> risposte) {
        this.risposte = risposte;
    }
}


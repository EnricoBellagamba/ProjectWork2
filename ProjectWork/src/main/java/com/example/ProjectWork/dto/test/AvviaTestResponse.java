package com.example.ProjectWork.dto.test;

public class AvviaTestResponse {
    private Long idTentativo;
    private Long idTest;
    private String iniziatoAt;

    public AvviaTestResponse(Long idTentativo, Long idTest, String iniziatoAt) {
        this.idTentativo = idTentativo;
        this.idTest = idTest;
        this.iniziatoAt = iniziatoAt;
    }

    public Long getIdTentativo() {
        return idTentativo;
    }

    public Long getIdTest() {
        return idTest;
    }

    public String getIniziatoAt() {
        return iniziatoAt;
    }
}

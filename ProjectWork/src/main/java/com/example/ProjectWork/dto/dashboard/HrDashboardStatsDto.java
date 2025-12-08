package com.example.ProjectWork.dto.dashboard;

public class HrDashboardStatsDto {

    private long totalePosizioni;
    private long totaleCandidature;
    private long totaleTest;
    private long totaleTentativi;

    public HrDashboardStatsDto() {
    }

    public HrDashboardStatsDto(long totalePosizioni, long totaleCandidature, long totaleTest, long totaleTentativi) {
        this.totalePosizioni = totalePosizioni;
        this.totaleCandidature = totaleCandidature;
        this.totaleTest = totaleTest;
        this.totaleTentativi = totaleTentativi;
    }

    public long getTotalePosizioni() {
        return totalePosizioni;
    }

    public void setTotalePosizioni(long totalePosizioni) {
        this.totalePosizioni = totalePosizioni;
    }

    public long getTotaleCandidature() {
        return totaleCandidature;
    }

    public void setTotaleCandidature(long totaleCandidature) {
        this.totaleCandidature = totaleCandidature;
    }

    public long getTotaleTest() {
        return totaleTest;
    }

    public void setTotaleTest(long totaleTest) {
        this.totaleTest = totaleTest;
    }

    public long getTotaleTentativi() {
        return totaleTentativi;
    }

    public void setTotaleTentativi(long totaleTentativi) {
        this.totaleTentativi = totaleTentativi;
    }
}

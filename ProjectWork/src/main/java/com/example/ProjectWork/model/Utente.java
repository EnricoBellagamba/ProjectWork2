package com.example.ProjectWork.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "UTENTE", schema = "dbo")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUtente;

    @Column(nullable = false)
    @Email(message = "Il formato dell'email inserita è sbagliato")
    @Pattern(regexp = ".+@.+\\..+", message = "Email deve avere un dominio valido")
    @NotBlank(message = "L'email è obbligatoria")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Devi inserire una password")
    private String passwordHash;

    @Column(nullable = false)
    @NotBlank
    private String nome;

    @Column(nullable = false)
    @NotBlank
    private String cognome;

    @Column(nullable = false)
    private Instant lastLogin; //memorizza un punto preciso nel tempo in UTC. Ottimo se vuoi uniformità e usi microservizi / più server.

    @Column(nullable = false)
    private LocalDate dataNascita;

    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    private String citta;

    @Column(nullable = false)
    private String lingua;

    @Column(nullable = false)
    private Boolean consensoPrivacy;

    @Column(nullable = false)
    private String cvUrl;

    @Column(nullable = false)
    private String cvHash;

    @ManyToOne
    @JoinColumn(name = "idRuolo")
    @JsonBackReference
    private Ruolo idRuolo;

    @OneToOne(mappedBy = "idUtente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Candidato candidato;

    @ManyToMany
    @JoinTable(
            name = "UTENTE_POSIZIONE_PREFERITA",
            joinColumns = @JoinColumn(name = "idUtente"),
            inverseJoinColumns = @JoinColumn(name = "idPosizione")
    )
    private List<Posizione> posizioniPreferite = new ArrayList<>();

    private Integer tokenVersion = 0;


    //GETTER E SETTER -----------------------------------------------------------------------------------------------

    public Long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(Long idUtente) {
        this.idUtente = idUtente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public Instant getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDate getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getLingua() {
        return lingua;
    }

    public void setLingua(String lingua) {
        this.lingua = lingua;
    }

    public Boolean getConsensoPrivacy() {
        return consensoPrivacy;
    }

    public void setConsensoPrivacy(Boolean consensoPrivacy) {
        this.consensoPrivacy = consensoPrivacy;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public String getCvHash() {
        return cvHash;
    }

    public void setCvHash(String cvHash) {
        this.cvHash = cvHash;
    }

    public Ruolo getIdRuolo() {
        return idRuolo;
    }

    public void setIdRuolo(Ruolo idRuolo) {
        this.idRuolo = idRuolo;
    }

    public List<Posizione> getPosizioniPreferite() {
        return posizioniPreferite;
    }

    public void setPosizioniPreferite(List<Posizione> posizioniPreferite) {
        this.posizioniPreferite = posizioniPreferite;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }

    public Integer getTokenVersion() {
        return tokenVersion;
    }

    public void setTokenVersion(Integer tokenVersion) {
        this.tokenVersion = tokenVersion;
    }
}
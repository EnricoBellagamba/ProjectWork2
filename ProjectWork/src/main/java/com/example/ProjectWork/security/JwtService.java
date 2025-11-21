package com.example.ProjectWork.security;

import com.example.ProjectWork.model.Utente;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    // Chiave segreta per HS256: almeno 32 caratteri
    // In produzione: spostare in application.properties o variabile d'ambiente
    private static final String SECRET_KEY =
            "candidai-secret-key-very-long-min-32-characters-123456";

    // Scadenze token
    private static final long ACCESS_EXP = 60L * 60L * 1000L;             // 1 ora
    private static final long REFRESH_EXP = 7L * 24L * 60L * 60L * 1000L; // 7 giorni

    private Key getKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // ===========================
    //       GENERAZIONE
    // ===========================

    public String generateAccessToken(Utente u) {
        return buildToken(u, ACCESS_EXP);
    }

    public String generateRefreshToken(Utente u) {
        return buildToken(u, REFRESH_EXP);
    }

    private String buildToken(Utente u, long expMs) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expMs);

        Map<String, Object> claims = new HashMap<>();
        if (u.getIdRuolo() != null) {
            claims.put("role", u.getIdRuolo().getCodice()); // "HR" / "CANDIDATO"
        }

        return Jwts.builder()
                .claims(claims)
                .subject(u.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ===========================
    //       VALIDAZIONE
    // ===========================

    public boolean isTokenValid(String token) {
        try {
            Claims c = getClaims(token);
            return c.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ===========================
    //       ESTRAZIONE DATI
    // ===========================

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public Date extractExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    // ===========================
    //       PARSING TOKEN
    // ===========================

    private Claims getClaims(String token) {
        // Forma compatibile con 0.12.x (anche se deprecata), ma semplice e funzionante
        return Jwts.parser()
                .setSigningKey(getKey())  // deprecated ma OK
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

package com.example.ProjectWork.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/ping")
    public String ping() { return "pong"; }

    @Autowired DataSource dataSource;

    @GetMapping("/db/ping")
    public String dbPing() throws Exception {
        try (Connection c = dataSource.getConnection()) {
            return "DB OK: " + c.getMetaData().getURL();
        }
    }

    @Autowired JdbcTemplate jdbc;

    @GetMapping("/db/tables")
    public List<String> tables() {
        return jdbc.query("SELECT name FROM sys.tables ORDER BY name",
                (rs, i) -> rs.getString(1));
    }
}

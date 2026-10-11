package br.com.clyvo.vitalia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.CallableStatement;
import java.sql.Connection;

@Service
@RequiredArgsConstructor
public class DatabaseProcedureService {

    private final JdbcTemplate jdbcTemplate;

    public void insertSpecies(String name) {

        jdbcTemplate.execute((Connection connection) -> {

            try (CallableStatement statement = connection.prepareCall(
                    "{call PKG_VITALIA_CARGA.PR_INSERT_SPECIES(?)}"
            )) {

                statement.setString(1, name);
                statement.execute();
            }

            return null;
        });
    }
}
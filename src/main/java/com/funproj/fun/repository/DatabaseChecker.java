package com.funproj.fun.repository;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Mono;

@Component
public class DatabaseChecker {
    private final DatabaseClient databaseClient;

    public DatabaseChecker(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @PostConstruct
    public void checkConnection() {
        databaseClient.sql("SELECT 1 FROM DUAL")
                .fetch()
                .rowsUpdated()
                .doOnSuccess(count -> System.out.println(" Database is reachable!"))
                .doOnError(error -> System.err.println(" Database connection failed: " + error.getMessage()))
                .subscribe();
    }
}
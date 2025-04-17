package com.funproj.fun.repository;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

/**
 * Component that verifies database connectivity during application startup.
 *
 * <p>This class performs a simple health check by executing a test query against
 * the database when the application context is initialized. The results are logged
 * to provide immediate feedback about database availability.
 *
 * <p><b>Usage Note:</b> The check runs automatically during application startup
 * due to the @PostConstruct annotation.
 */
@Component
public class DatabaseChecker {
    private final DatabaseClient databaseClient;

    /**
     * Constructs a new DatabaseChecker with the required DatabaseClient.
     *
     * @param databaseClient the reactive database client used to execute health check queries
     */
    public DatabaseChecker(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    /**
     * Executes a simple database health check immediately after bean initialization.
     *
     * <p>This method:
     * <ol>
     *   <li>Executes a test query (SELECT 1)</li>
     *   <li>Logs success message if database is reachable</li>
     *   <li>Logs error message if connection fails</li>
     * </ol>
     *
     * <p><b>Implementation Notes:</b>
     * <ul>
     *   <li>Uses a simple query compatible with most database systems</li>
     *   <li>The 'DUAL' table is Oracle-specific but works with many databases</li>
     *   <li>For H2/MySQL, consider using "SELECT 1" without FROM clause</li>
     *   <li>Results are logged to standard output/error streams</li>
     * </ul>
     */
    @PostConstruct // PostConstruct methods executed immediately after bean fully initialized and all dependencies have been injected
    public void checkConnection() {
        databaseClient.sql("SELECT 1 FROM DUAL")
                .fetch()
                .rowsUpdated()
                .doOnSuccess(count -> System.out.println(" Database is reachable!"))
                .doOnError(error -> System.err.println(" Database connection failed: " + error.getMessage()))
                .subscribe();
    }
}
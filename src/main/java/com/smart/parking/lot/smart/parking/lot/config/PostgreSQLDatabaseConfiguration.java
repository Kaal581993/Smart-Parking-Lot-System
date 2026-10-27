package com.smart.parking.lot.smart.parking.lot.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates the configured PostgreSQL database before Hibernate initializes.
 * PostgreSQL cannot create a database while connected to that same database,
 * so the bootstrap first connects to the maintenance database (postgres).
 */
@Configuration
@Profile({"pgvector", "prod"})
public class PostgreSQLDatabaseConfiguration {

    private static final String POSTGRESQL_JDBC_PREFIX = "jdbc:postgresql://";
    private static final String MAINTENANCE_DATABASE = "postgres";

    @Bean
    @Primary
    public DataSource dataSource(Environment environment) {
        String jdbcUrl = requiredProperty(environment, "spring.datasource.url");
        String username = requiredProperty(environment, "spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password", "");

        ensureDatabaseExists(jdbcUrl, username, password);
        ensureVectorExtension(jdbcUrl, username, password);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(hikariConfig);
    }

    private void ensureDatabaseExists(String jdbcUrl, String username, String password) {
        DatabaseTarget target = DatabaseTarget.from(jdbcUrl);

        try (Connection connection = DriverManager.getConnection(
                target.maintenanceUrl(), username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT 1 FROM pg_database WHERE datname = ?")) {
            statement.setString(1, target.databaseName());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return;
                }
            }

            try (Statement createStatement = connection.createStatement()) {
                createStatement.execute("CREATE DATABASE " + quoteIdentifier(target.databaseName()));
            }
        } catch (SQLException exception) {
            // Another application instance may create the database between the
            // existence check and CREATE DATABASE.
            if ("42P04".equals(exception.getSQLState())) {
                return;
            }
            throw new IllegalStateException(
                    "Unable to create or access PostgreSQL database '" + target.databaseName()
                            + "'. Ensure the configured user can connect to the '"
                            + MAINTENANCE_DATABASE + "' database and create databases.",
                    exception);
        }
    }

    private void ensureVectorExtension(String jdbcUrl, String username, String password) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE EXTENSION IF NOT EXISTS vector");
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Unable to enable the pgvector extension. Install pgvector on PostgreSQL "
                            + "and ensure the configured user can create extensions.",
                    exception);
        }
    }

    private String requiredProperty(Environment environment, String propertyName) {
        String value = environment.getProperty(propertyName);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(propertyName + " must be configured for PostgreSQL");
        }
        return value;
    }

    private String quoteIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    private record DatabaseTarget(String databaseName, String maintenanceUrl) {

        private static DatabaseTarget from(String jdbcUrl) {
            if (!jdbcUrl.startsWith(POSTGRESQL_JDBC_PREFIX)) {
                throw new IllegalArgumentException(
                        "spring.datasource.url must be a PostgreSQL JDBC URL: " + jdbcUrl);
            }

            int authorityEnd = jdbcUrl.indexOf('/', POSTGRESQL_JDBC_PREFIX.length());
            if (authorityEnd < 0 || authorityEnd == jdbcUrl.length() - 1) {
                throw new IllegalArgumentException(
                        "PostgreSQL JDBC URL must include a database name: " + jdbcUrl);
            }

            int queryStart = jdbcUrl.indexOf('?', authorityEnd + 1);
            int databaseEnd = queryStart >= 0 ? queryStart : jdbcUrl.length();
            String databaseName = jdbcUrl.substring(authorityEnd + 1, databaseEnd);
            if (databaseName.isBlank()) {
                throw new IllegalArgumentException(
                        "PostgreSQL JDBC URL must include a database name: " + jdbcUrl);
            }

            String maintenanceUrl = jdbcUrl.substring(0, authorityEnd + 1)
                    + MAINTENANCE_DATABASE
                    + (queryStart >= 0 ? jdbcUrl.substring(queryStart) : "");
            return new DatabaseTarget(databaseName, maintenanceUrl);
        }
    }
}

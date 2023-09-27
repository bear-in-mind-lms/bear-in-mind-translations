package com.kwezal.bearinmind.translation.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
public class DataSourceConfiguration {

    @Bean
    DataSource dataSource() {
        final var postgresContainer = createPostgresContainer();
        postgresContainer.start();

        final var dataSource = new HikariDataSource();
        dataSource.setMaxLifetime(30000);
        dataSource.setConnectionTimeout(30000);
        dataSource.setMaximumPoolSize(20);
        dataSource.setJdbcUrl(postgresContainer.getJdbcUrl());
        dataSource.setUsername(postgresContainer.getUsername());
        dataSource.setPassword(postgresContainer.getPassword());
        dataSource.setDriverClassName(postgresContainer.getDriverClassName());
        return dataSource;
    }

    private PostgreSQLContainer<?> createPostgresContainer() {
        return new PostgreSQLContainer<>("postgres:14")
            .withUsername("postgres")
            .withPassword("postgres")
            .withCommand("postgres -c fsync=off -c max_connections=300")
            .waitingFor(new org.testcontainers.containers.wait.strategy.HostPortWaitStrategy());
    }
}

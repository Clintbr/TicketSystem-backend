package com.service.springbackend.testdb;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    private final DataSource dataSource;

    public DatabaseConnectionTest(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        System.out.println("Testing database connection...");
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(5)) {
                System.out.println("Database connection successful ✅");
            } else {
                System.out.println("Database connection failed ❌");
            }
        } catch (Exception e) {
            System.out.println("Database connection failed ❌");
            e.printStackTrace();
        }
    }
}
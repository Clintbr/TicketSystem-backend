package com.service.springbackend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DotEnvPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            String springbackendDir = Paths.get(".").toAbsolutePath().toString();
            Dotenv dotenv = Dotenv.configure()
                    .directory(springbackendDir)
                    .load();

            Map<String, Object> properties = new HashMap<>();

            String[] envVars = {
                    "DB_HOST", "DB_PORT", "DB_NAME", "DB_USER", "DB_PASSWORD", "SUPABASE_PROJEKT_ID",
                    "SUPABASE_JWT_SECRET"
            };

            for (String var : envVars) {
                String value = dotenv.get(var);
                if (value != null) {
                    properties.put(var, value);
                    System.out.println("✅ " + var + " geladen");
                }
            }

            environment.getPropertySources().addFirst(
                    new MapPropertySource("dotenv", properties)
            );
            System.out.println("✅ .env vollständig geladen und zu Spring Properties hinzugefügt!");
        } catch (Exception e) {
            System.out.println("⚠️ .env-Datei nicht gefunden: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
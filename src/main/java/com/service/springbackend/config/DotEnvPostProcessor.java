package com.service.springbackend.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotEnvPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();

        Map<String, Object> properties = new HashMap<>();

        String[] envVars = {
                "DB_HOST", "DB_PORT", "DB_NAME", "DB_USER", "DB_PASSWORD",
                "SUPABASE_PROJEKT_ID", "SUPABASE_JWT_SECRET"
        };

        boolean fileFound = false;
        for (String var : envVars) {
            String value = dotenv.get(var);
            if (value != null) {
                properties.put(var, value);
                System.out.println("✅ " + var + " geladen \n");
                fileFound = true;
            } else {
                System.out.println("❌ " + var + "nicht lokal gefunden \n");
                fileFound = false;
            }
        }

        if (fileFound) {
            environment.getPropertySources().addFirst(
                    new MapPropertySource("dotenvProperties", properties)
            );
            System.out.println("✅ Lokale .env-Variablen wurden geladen.");
        } else {
            System.out.println("ℹ️ Keine .env-Datei gefunden oder Variablen leer. Nutze System-Umgebungsvariablen.");
        }
    }
}
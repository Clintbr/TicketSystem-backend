package com.service.springbackend.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/test/public").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authenticationEntryPoint())
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5174","http://localhost:5173", "http://localhost:3000", "https://ticketsystem-frontend-p9y9.onrender.com"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));

        configuration.setExposedHeaders(List.of("Authorization"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(
                    "{\"status\": 401, \"message\": \"Token abgelaufen oder ungültig\", \"timestamp\": "
                            + System.currentTimeMillis() + "}"
            );
        };
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String issuerUri = "https://ekuljraojhsozqzwtcya.supabase.co/auth/v1";
        String jwkSetUri = issuerUri + "/.well-known/jwks.json";

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .jwsAlgorithm(org.springframework.security.oauth2.jose.jws.SignatureAlgorithm.ES256)
                .build();

        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<List<String>>(
                JwtClaimNames.AUD, aud -> aud != null && aud.contains("authenticated")
        );

        OAuth2TokenValidator<Jwt> combinedValidator = new DelegatingOAuth2TokenValidator<>(
                issuerValidator, audienceValidator
        );

        jwtDecoder.setJwtValidator(combinedValidator);

        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (JwtException e) {
                System.err.println("--- JWT VALIDIERUNGSFEHLER ---");
                System.err.println("Fehler-Typ: " + e.getClass().getSimpleName());
                System.err.println("Ursache: " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("Detail-Grund: " + e.getCause().getMessage());
                }
                throw e;
            }
        };
    }
}
package com.service.springbackend.dto;

import java.util.UUID;

public record AuthResponse(
        UUID id,
        String username,
        String email,
        String role,
        String message // Neues Feld für die Statusmeldung
) {}
package com.service.springbackend.dto;

import java.util.UUID;

public record LoginResponse(
        UUID id,
        String email,
        String username,
        com.service.springbackend.model.Role role,
        String token
) {}

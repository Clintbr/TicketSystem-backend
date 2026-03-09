package com.service.springbackend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String role,
        LocalDateTime createdAt
) {}
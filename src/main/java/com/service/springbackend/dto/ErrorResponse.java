package com.service.springbackend.dto;

public record ErrorResponse(
        int status,
        String message,
        long timestamp
) {}

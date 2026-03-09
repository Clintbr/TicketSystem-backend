package com.service.springbackend.dto;

import com.service.springbackend.model.Priority;
import com.service.springbackend.model.Status;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        String title,
        String description,
        Status status,
        Priority priority,
        String createdByUsername,
        String assignedToUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
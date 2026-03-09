package com.service.springbackend.dto;

import com.service.springbackend.model.Priority;

public record TicketRequest(
        String title,
        String description,
        Priority priority
) {}

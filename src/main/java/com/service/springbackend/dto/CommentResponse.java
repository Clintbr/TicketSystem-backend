package com.service.springbackend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID ticketId,
        String authorUsername,
        String authorMail,
        String content,
        LocalDateTime createdAt
) {}
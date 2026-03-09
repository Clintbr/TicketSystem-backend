package com.service.springbackend.dto;

public record DashboardStatsResponse(
        Long totalUsers,
        Long totalTickets,
        Long openTickets,
        Long closedTickets,
        Long inProgressTickets
) { }

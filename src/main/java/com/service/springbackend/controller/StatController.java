package com.service.springbackend.controller;

import com.service.springbackend.dto.DashboardStatsResponse;
import com.service.springbackend.dto.TicketHistoryDTO;
import com.service.springbackend.dto.UserResponse;
import com.service.springbackend.model.Role;
import com.service.springbackend.model.Status;
import com.service.springbackend.model.User;
import com.service.springbackend.repository.TicketRepository;
import com.service.springbackend.repository.UserRepository;
import com.service.springbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class StatController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    StatController(UserService userService, UserRepository userRepository, TicketRepository ticketRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats(@AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString("email");
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin nicht gefunden"));

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Zugriff verweigert: Du bist kein Admin");
        }
       DashboardStatsResponse response = new DashboardStatsResponse(userRepository.count(), ticketRepository.count(), ticketRepository.countByStatus(Status.OPEN), ticketRepository.countByStatus(Status.CLOSED),ticketRepository.countByStatus(Status.IN_PROGRESS));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/stats/history")
    public ResponseEntity<List<TicketHistoryDTO>> getHistoryStats(@AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString("email");
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin nicht gefunden"));

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Zugriff verweigert: Du bist kein Admin");
        }
        return ResponseEntity.ok(ticketRepository.getTicketHistory());
    }
}

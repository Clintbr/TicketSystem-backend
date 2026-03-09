package com.service.springbackend.controller;

import com.service.springbackend.dto.StatusUpdateRequest;
import com.service.springbackend.dto.TicketRequest;
import com.service.springbackend.dto.TicketResponse;
import com.service.springbackend.dto.UserResponse;
import com.service.springbackend.model.Priority;
import com.service.springbackend.model.Role;
import com.service.springbackend.model.Ticket;
import com.service.springbackend.model.User;
import com.service.springbackend.repository.UserRepository;
import com.service.springbackend.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets(@AuthenticationPrincipal Jwt jwt) {
        List<TicketResponse> responses = ticketService.getAllTickets(jwt);
        return ResponseEntity.ok(responses);
    }

    /**
     *
     * @param request
     * @param jwt
     * @return
     */
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@RequestBody TicketRequest request,
                                                       @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ticketService.createTicket(request, jwt));
    }

    /**
     *
     * @param jwt
     * @return
     */
    @GetMapping("/my")
    public ResponseEntity<List<TicketResponse>> getMyTickets(@AuthenticationPrincipal Jwt jwt) {
        List<TicketResponse> responses = ticketService.getAllMyTickets(jwt);
        return ResponseEntity.ok(responses);
    }

    /**
     *
     * @param jwt
     * @return
     */
    @GetMapping("/assigned")
    public ResponseEntity<List<TicketResponse>> getAssignedTickets(@AuthenticationPrincipal Jwt jwt) {
        List<TicketResponse> responses = ticketService.getTicketsAssignedToMe(jwt);
        return ResponseEntity.ok(responses);
    }

    /**
     *
     * @param id
     * @param jwt
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable UUID id,
                                                        @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ticketService.getTicketById(id, jwt));
    }

    /**
     *
     * @param id
     * @param supportId
     * @param jwt
     * @return
     */
    @PatchMapping("/{id}/assign/{supportId}")
    public ResponseEntity<TicketResponse> assignTicket(@PathVariable UUID id,
                                                       @PathVariable UUID supportId,
                                                       @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ticketService.assignTicketToUser(id, supportId, jwt));
    }

    @PatchMapping("/{id}/unassign")
    public ResponseEntity<TicketResponse> unassignTicket(@PathVariable UUID id,
                                                       @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ticketService.unassignTicket(id,jwt));
    }

    /**
     *
     * @param id
     * @param request
     * @param jwt
     * @return
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(ticketService.updateTicketStatus(id, request.status(), jwt));
    }

    /**
     *
     * @param id
     * @param newPrio
     * @param jwt
     * @return
     */
    @PatchMapping("/{id}/priority")
    public ResponseEntity<TicketResponse> updateTicketPriority(
            @PathVariable UUID id,
            @RequestParam Priority newPrio,
            @AuthenticationPrincipal Jwt jwt) {

        return ResponseEntity.ok(ticketService.updateTicketPriority(id, newPrio, jwt));
    }

    /**
     *
     * @param id
     * @param jwt
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id,
                                             @AuthenticationPrincipal Jwt jwt) {
        ticketService.deleteTicket(id, jwt);
        return ResponseEntity.noContent().build();
    }
}
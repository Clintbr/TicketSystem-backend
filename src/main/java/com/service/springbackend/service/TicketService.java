package com.service.springbackend.service;

import com.service.springbackend.ai.TicketPriorityService;
import com.service.springbackend.dto.TicketRequest;
import com.service.springbackend.dto.TicketResponse;
import com.service.springbackend.model.*;
import com.service.springbackend.repository.TicketRepository;
import com.service.springbackend.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final TicketPriorityService priorityService;

    TicketService(TicketRepository ticketRepository, UserRepository userRepository, TicketPriorityService priorityService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.priorityService = priorityService;
    }

    public List<TicketResponse> getAllMyTickets(Jwt jwt) {
        User currentUser = getCurrentUser(jwt);
        return ticketRepository.findByCreatedBy(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<TicketResponse> getAllTickets(Jwt jwt) {
        User currentUser = getCurrentUser(jwt);
        boolean hasAccess = currentUser.getRole().equals(Role.ADMIN) || currentUser.getRole().equals(Role.SUPPORT);

        if (!hasAccess) throw new RuntimeException("Zugriff verweigert: Sie sind kein Support oder Admin");

        return ticketRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TicketResponse getTicketById(UUID id, Jwt jwt) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket mit der ID " + id + " wurde nicht gefunden."));
        return mapToResponse(ticket);
    }

    @Transactional
    public TicketResponse createTicket(TicketRequest request, Jwt jwt) {
        User currentUser = getCurrentUser(jwt);

        Ticket ticket = new Ticket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setStatus(Status.OPEN);
        Priority priority = priorityService.analyzePriority(
                request.title() + " " + request.description()
        );
        ticket.setPriority(priority);
        ticket.setCreatedBy(currentUser);

        Ticket saved = ticketRepository.save(ticket);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteTicket(UUID id, Jwt jwt) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket nicht gefunden."));

        String currentUserEmail = jwt.getClaimAsString("email");
        if (!ticket.getCreatedBy().getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("Zugriff verweigert: Sie sind nicht der Besitzer dieses Tickets.");
        }

        ticketRepository.delete(ticket);
    }

    public List<TicketResponse> getTicketsAssignedToMe(Jwt jwt) {
        User currentUser = getCurrentUser(jwt);
        return ticketRepository.findByAssignedTo(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public TicketResponse assignTicketToUser(UUID ticketId, UUID supportId, Jwt jwt) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket mit der ID " + ticketId + " nicht gefunden."));

        User assignedSupportUser = userRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden"));
        User supportUser = getCurrentUser(jwt);

        boolean hasAccess = supportUser.getRole().equals(Role.ADMIN) || supportUser.getRole().equals(Role.SUPPORT);
        if (!hasAccess) {
            throw new RuntimeException("Zugriff verweigert: Sie sind kein Support oder Admin");
        }
        else if(ticket.getCreatedBy().equals(assignedSupportUser)) throw new RuntimeException("Zuweisung ungültig: Das Ticket gehört Ihnen");

        ticket.setAssignedTo(assignedSupportUser);
        ticket.setStatus(Status.IN_PROGRESS);

        Ticket savedTicket = ticketRepository.save(ticket);

        return mapToResponse(savedTicket);
    }

    @Transactional
    public TicketResponse unassignTicket(UUID ticketId, Jwt jwt) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket mit der ID " + ticketId + " nicht gefunden."));


        User assignedSupportUser = ticket.getAssignedTo();
        if(assignedSupportUser == null) throw new RuntimeException("Aktion ungültig: Ticket ist nicht zugewiesen");
        User supportUser = getCurrentUser(jwt);

        boolean hasAccess = supportUser.getRole().equals(Role.ADMIN) || (supportUser.getRole().equals(Role.SUPPORT) && supportUser.getId() == assignedSupportUser.getId());
        if (!hasAccess) {
            throw new RuntimeException("Zugriff verweigert: Sie sind kein Admin oder das Ticket ist Ihnen nicht zugewiesen");
        }

        ticket.setAssignedTo(null);
        ticket.setStatus(Status.OPEN);
        Ticket savedTicket = ticketRepository.save(ticket);

        return mapToResponse(savedTicket);
    }

    @Transactional
    public TicketResponse updateTicketStatus(UUID id, Status status, Jwt jwt) {
        User user = getCurrentUser(jwt);

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPPORT) {
            throw new RuntimeException("Nicht autorisiert: Nur Support oder Admins dürfen Statusänderungen vornehmen.");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket nicht gefunden."));

        ticket.setStatus(status);
        return mapToResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public TicketResponse updateTicketPriority(UUID id, Priority priority, Jwt jwt) {
        User user = getCurrentUser(jwt);

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPPORT) {
            throw new RuntimeException("Nicht autorisiert: Nur Support oder Admins dürfen Priority-Änderungen vornehmen.");
        }

        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket nicht gefunden."));

        ticket.setPriority(priority);
        return mapToResponse(ticketRepository.save(ticket));
    }


    private User getCurrentUser(Jwt jwt) {
        if (jwt == null) {
            throw new RuntimeException("Nicht authentifiziert: Token fehlt.");
        }
        String email = jwt.getClaimAsString("email");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Benutzerprofil in der Datenbank nicht gefunden."));
    }

    public TicketResponse mapToResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCreatedBy().getUsername(),
                ticket.getAssignedTo() != null ? ticket.getAssignedTo().getUsername() : null,
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
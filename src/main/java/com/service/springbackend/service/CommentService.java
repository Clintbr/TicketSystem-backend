package com.service.springbackend.service;

import com.service.springbackend.dto.CommentRequest;
import com.service.springbackend.dto.CommentResponse;
import com.service.springbackend.model.*;
import com.service.springbackend.repository.CommentRepository;
import com.service.springbackend.repository.TicketRepository;
import com.service.springbackend.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    CommentService(CommentRepository commentRepository, TicketRepository ticketRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CommentResponse createComment(UUID ticketId, CommentRequest request, Jwt jwt) {
        User user = getCurrentUser(jwt);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket nicht gefunden"));
        if(ticket.getStatus().equals(Status.CLOSED))
            throw new RuntimeException("Ticket ist schon geschloßen");
        assert user.getId() != null;
        if(!user.getId().equals(ticket.getCreatedBy().getId()) && user.getRole().equals(Role.USER))
            throw new RuntimeException("Das Ticket gehört Ihnen nicht");
        if(ticket.getAssignedTo() == null) {
            if(user.getRole().equals(Role.SUPPORT) && !user.getId().equals(ticket.getCreatedBy().getId()))
                throw new RuntimeException("Das Ticket ist noch niemandem zugewiesen");
        } else if(!user.getId().equals(ticket.getAssignedTo().getId()) && user.getRole().equals(Role.SUPPORT))
            throw new RuntimeException("Das Ticket ist Ihnen nicht zugewiesen");

        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setTicket(ticket);
        comment.setAuthor(user);


        Comment saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }

    public List<CommentResponse> getCommentsByTicket(UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket nicht gefunden"));

        return commentRepository.findByTicketOrderByCreatedAtAsc(ticket)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deleteComment(UUID commentId, Jwt jwt) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Kommentar nicht gefunden"));

        if (!comment.getAuthor().getEmail().equals(jwt.getClaimAsString("email"))) {
            throw new RuntimeException("Du darfst nur deine eigenen Kommentare löschen!");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getTicket().getId(),
                comment.getAuthor().getUsername(),
                comment.getAuthor().getEmail(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }

    private User getCurrentUser(Jwt jwt) {
        return userRepository.findByEmail(jwt.getClaimAsString("email"))
                .orElseThrow(() -> new RuntimeException("User nicht gefunden"));
    }
}
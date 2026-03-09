package com.service.springbackend.controller;

import com.service.springbackend.dto.CommentRequest;
import com.service.springbackend.dto.CommentResponse;
import com.service.springbackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID ticketId,
            @RequestBody CommentRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(commentService.createComment(ticketId, request, jwt));
    }

    @GetMapping("/tickets/{ticketId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable UUID ticketId) {
        return ResponseEntity.ok(commentService.getCommentsByTicket(ticketId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId,
            @AuthenticationPrincipal Jwt jwt) {
        commentService.deleteComment(commentId, jwt);
        return ResponseEntity.noContent().build();
    }
}
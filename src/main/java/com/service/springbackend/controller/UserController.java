package com.service.springbackend.controller;

import com.service.springbackend.dto.UserResponse;
import com.service.springbackend.model.Role;
import com.service.springbackend.model.User;
import com.service.springbackend.repository.UserRepository;
import com.service.springbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString("email");
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin nicht gefunden"));

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Zugriff verweigert: Du bist kein Admin");
        }
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable UUID id,
            @RequestParam Role newRole,
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString("email");
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin nicht gefunden"));

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Zugriff verweigert: Du bist kein Admin");
        }

        return ResponseEntity.ok(userService.updateRole(id, newRole));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin nicht gefunden"));

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Zugriff verweigert: Du bist kein Admin");
        }
        userService.deleteUser(id);
    }
}
package com.service.springbackend.controller;

import com.service.springbackend.dto.AuthResponse;
import com.service.springbackend.dto.LoginRequest;
import com.service.springbackend.dto.LoginResponse;
import com.service.springbackend.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.service.springbackend.service.Authservice;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Authservice authService;

    AuthController(Authservice authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Das Backend holt das Token von Supabase und reicht es an das Frontend weiter
        return ResponseEntity.ok(authService.login(request));
    }
}
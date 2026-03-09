package com.service.springbackend.service;

import com.service.springbackend.dto.UserResponse;
import com.service.springbackend.model.Role;
import com.service.springbackend.model.User;
import com.service.springbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateRole(UUID id, Role newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        user.setRole(newRole);
        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(UUID id) {
        userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User nicht gefunden"));

        userRepository.deleteUserNative(id);
        userRepository.flush();
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
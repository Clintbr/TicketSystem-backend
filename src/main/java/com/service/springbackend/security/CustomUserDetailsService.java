package com.service.springbackend.security;

import com.service.springbackend.model.Role;
import com.service.springbackend.model.User;
import com.service.springbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Suche den User in der lokalen Datenbank
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // 2. Wenn nicht gefunden: Automatisch anlegen (Sync mit Supabase)
                    System.out.println("Neuer User erkannt: " + email + ". Lege lokalen Datensatz an...");

                    User newUser = new User();
                    newUser.setId(UUID.randomUUID()); // Oder die UUID aus dem JWT extrahieren, falls gewünscht
                    newUser.setEmail(email);
                    // Vorübergehender Username (Teil vor dem @)
                    newUser.setUsername(email.split("@")[0]);
                    newUser.setRole(Role.USER); // Standardrolle vergeben
                    newUser.setCreatedAt(LocalDateTime.now());

                    // In der DB speichern
                    return userRepository.save(newUser);
                });
    }
}
package com.service.springbackend.repository;

import com.service.springbackend.model.Role;
import com.service.springbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    @Modifying
    @Query(value = "DELETE FROM users WHERE id = :id", nativeQuery = true)
    void deleteUserNative(@Param("id") UUID id);
}
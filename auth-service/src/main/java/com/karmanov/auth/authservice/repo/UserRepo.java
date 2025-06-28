package com.karmanov.auth.authservice.repo;

import com.karmanov.auth.authservice.model.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepo extends JpaRepository<UserEntity, UUID> {
    boolean existsByKeycloakId(String keycloakId);

    @EntityGraph(attributePaths = {"createdRooms", "rooms"})
    UserEntity findByKeycloakId(String keycloakId);
}

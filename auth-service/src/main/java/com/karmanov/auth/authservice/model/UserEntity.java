package com.karmanov.auth.authservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String keycloakId;

    @OneToMany(mappedBy = "owner")
    private Set<RoomEntity> createdRooms = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_room",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    private Set<RoomEntity> rooms = new HashSet<>();

    public UserEntity(String keycloakId, Set<RoomEntity> createdRooms, Set<RoomEntity> rooms) {
        this.keycloakId = keycloakId;
        this.createdRooms = createdRooms;
        this.rooms = rooms;
    }
}

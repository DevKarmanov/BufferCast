package com.karmanov.auth.authservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @ManyToOne
    private UserEntity owner;

    @ManyToMany(mappedBy = "rooms")
    private Set<UserEntity> users = new HashSet<>();

    public RoomEntity(String name, UserEntity owner, Set<UserEntity> users) {
        this.name = name;
        this.owner = owner;
        this.users = users;
    }
}

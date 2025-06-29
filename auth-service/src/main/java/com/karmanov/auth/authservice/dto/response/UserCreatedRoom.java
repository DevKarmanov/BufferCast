package com.karmanov.auth.authservice.dto.response;

import java.util.UUID;

public record UserCreatedRoom(UUID id, String name) {
}

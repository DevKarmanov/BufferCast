package com.karmanov.auth.dto.response;

import java.util.UUID;

public record UserCreatedRoom(UUID id, String name) {
}

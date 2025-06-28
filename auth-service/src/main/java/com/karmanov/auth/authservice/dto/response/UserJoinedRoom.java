package com.karmanov.auth.authservice.dto.response;

import java.util.UUID;

public record UserJoinedRoom(UUID id, String name) {
}

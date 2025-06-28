package com.karmanov.auth.authservice.dto.response;

import java.util.List;

public record UserProfileInfo(String keycloakId, List<UserCreatedRoom> createdRooms, List<UserJoinedRoom> rooms) {
}

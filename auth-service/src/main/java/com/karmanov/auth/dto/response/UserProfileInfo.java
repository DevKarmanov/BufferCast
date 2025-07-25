package com.karmanov.auth.dto.response;

import java.util.List;
import java.util.UUID;

public record UserProfileInfo(UUID id, List<UserCreatedRoom> createdRooms, List<UserJoinedRoom> rooms) {
}

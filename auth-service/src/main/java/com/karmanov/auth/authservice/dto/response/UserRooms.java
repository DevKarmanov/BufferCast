package com.karmanov.auth.authservice.dto.response;

import java.util.List;

public record UserRooms(List<UserCreatedRoom> createdRooms, List<UserJoinedRoom> joinedRooms) {
}

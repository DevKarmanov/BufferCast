package com.karmanov.auth.dto.response;

import java.util.List;

public record UserRooms(List<UserCreatedRoom> createdRooms, List<UserJoinedRoom> joinedRooms) {
}

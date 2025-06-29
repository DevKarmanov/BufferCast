package com.karmanov.auth.service.user;

import com.karmanov.auth.dto.response.UserProfileInfo;
import com.karmanov.auth.dto.response.UserRooms;

import java.util.UUID;

public interface UserService {
    UserProfileInfo getOrCreateUser(String keycloakId);
    UserProfileInfo getUserInfo(UUID userId);
    UserRooms getUserRooms(UUID userId);
}

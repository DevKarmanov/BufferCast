package com.karmanov.auth.authservice.service.user;

import com.karmanov.auth.authservice.dto.response.UserProfileInfo;
import com.karmanov.auth.authservice.dto.response.UserRooms;

import java.util.UUID;

public interface UserService {
    UserProfileInfo getOrCreateUser(String keycloakId);
    UserProfileInfo getUserInfo(UUID userId);
    UserRooms getUserRooms(UUID userId);
}

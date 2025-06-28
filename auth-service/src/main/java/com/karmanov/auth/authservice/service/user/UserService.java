package com.karmanov.auth.authservice.service.user;

import com.karmanov.auth.authservice.dto.response.UserProfileInfo;
import com.karmanov.auth.authservice.model.UserEntity;

public interface UserService {
    UserProfileInfo getOrCreateUser(String keycloakId);
}

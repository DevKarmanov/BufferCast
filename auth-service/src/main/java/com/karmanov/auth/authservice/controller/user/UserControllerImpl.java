package com.karmanov.auth.authservice.controller.user;

import com.karmanov.auth.authservice.dto.response.UserProfileInfo;
import com.karmanov.auth.authservice.dto.response.UserRooms;
import com.karmanov.auth.authservice.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserControllerImpl implements UserController {
    private final UserService userService;

    public UserControllerImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<UserProfileInfo> getCurrentUserInfo(Authentication authentication){
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getSubject();

        return ResponseEntity.ok(userService.getOrCreateUser(keycloakId));
    }

    @Override
    public ResponseEntity<UserProfileInfo> getUserInfo(UUID id) {
        return ResponseEntity.ok(userService.getUserInfo(id));
    }

    @Override
    public ResponseEntity<UserRooms> getUserRooms(UUID id) {
        return ResponseEntity.ok(userService.getUserRooms(id));
    }
}


package com.karmanov.auth.controller.user;

import com.karmanov.auth.dto.response.UserProfileInfo;
import com.karmanov.auth.dto.response.UserRooms;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/users")
public interface UserController {

    @GetMapping("/me")
    ResponseEntity<UserProfileInfo> getCurrentUserInfo(Authentication authentication);

    @GetMapping("/{id}")
    ResponseEntity<UserProfileInfo> getUserInfo(@PathVariable UUID id);

    @GetMapping("/{id}/rooms")
    ResponseEntity<UserRooms> getUserRooms(@PathVariable UUID id);
}

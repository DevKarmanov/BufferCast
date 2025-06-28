package com.karmanov.auth.authservice.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping
public interface UserController {

    @GetMapping("/me")
    ResponseEntity<?> getProfileInformation(Authentication authentication);
}

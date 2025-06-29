package com.karmanov.auth.controller.oauth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping
public interface OAuth2Controller {
    @GetMapping("/callback")
    ResponseEntity<String> getAuthCode(@RequestParam String code);
    @PostMapping("/verify-token")
    ResponseEntity<Void> verifyToken(Authentication authentication);
}

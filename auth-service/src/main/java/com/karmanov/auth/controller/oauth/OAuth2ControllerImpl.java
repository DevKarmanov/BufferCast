package com.karmanov.auth.controller.oauth;

import com.karmanov.auth.service.oauth.OauthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

//todo перенести это на десктоп часть c получением токенов и хранить токены в куки
@RestController
public class OAuth2ControllerImpl implements OAuth2Controller{
    private static final Logger log = LoggerFactory.getLogger(OAuth2ControllerImpl.class);
    private final OauthService oauthService;

    public OAuth2ControllerImpl(OauthService oauthService) {
        this.oauthService = oauthService;
    }

    @Override
    public ResponseEntity<String> getAuthCode(String code) {
        log.debug("Code received: {}",code);
        return ResponseEntity.ok(oauthService.getTokens(code));
    }

    @Override
    public ResponseEntity<Void> verifyToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }

}


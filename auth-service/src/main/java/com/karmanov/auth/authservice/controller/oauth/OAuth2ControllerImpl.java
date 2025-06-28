package com.karmanov.auth.authservice.controller.oauth;

import com.karmanov.auth.authservice.service.oauth.OauthService;
import com.karmanov.auth.authservice.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RestController;

//todo перенести это на десктоп часть и хранить токены в куки
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

        try {
            return ResponseEntity.ok(oauthService.getTokens(code));
        }catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}


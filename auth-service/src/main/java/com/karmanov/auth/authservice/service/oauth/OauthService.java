package com.karmanov.auth.authservice.service.oauth;

public interface OauthService {
    String getTokens(String code);
}

package com.karmanov.auth.authservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleOAuthResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") int expiresIn,
        @JsonProperty("refresh_expires_in") int refreshExpiresIn,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("id_token") String idToken,
        @JsonProperty("not-before-policy") int notBeforePolicy,
        @JsonProperty("session_state") String sessionState,
        @JsonProperty("scope") String scope
) {}
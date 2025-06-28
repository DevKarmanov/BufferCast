package com.karmanov.auth.authservice.service.oauth;

import com.karmanov.auth.authservice.config.props.ClientProperties;
import com.karmanov.auth.authservice.dto.response.GoogleOAuthResponse;
import com.karmanov.auth.authservice.service.request.RequestSender;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class OauthServiceImpl implements OauthService{
    private static final Logger log = LoggerFactory.getLogger(OauthServiceImpl.class);
    private final ClientProperties clientProperties;
    private final RequestSender requestSender;

    public OauthServiceImpl(ClientProperties clientProperties, RequestSender requestSender) {
        this.clientProperties = clientProperties;
        this.requestSender = requestSender;
    }

    @PostConstruct
    public void test() {
        log.info("clientId = " + clientProperties.getClientId());
    }

    @Override
    public String getTokens(String code){
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("client_id", clientProperties.getClientId());
        form.add("client_secret", clientProperties.getClientSecret());
        form.add("redirect_uri", clientProperties.getRedirectUri());

        GoogleOAuthResponse response = requestSender.sendRequest(
                MediaType.APPLICATION_FORM_URLENCODED,
                form,
                "http://localhost:8080/realms/BufferCast/protocol/openid-connect/token",
                GoogleOAuthResponse.class);

        return response.toString();
    }
}

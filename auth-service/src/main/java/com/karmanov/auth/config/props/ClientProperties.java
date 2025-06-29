package com.karmanov.auth.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "keycloak")
public class ClientProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;

}

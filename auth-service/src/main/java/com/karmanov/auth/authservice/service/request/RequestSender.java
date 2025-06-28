package com.karmanov.auth.authservice.service.request;

import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

public interface RequestSender {
    <T> T sendRequest(MediaType contentType, MultiValueMap<String, String> form, String url, Class<T> responseType);
}

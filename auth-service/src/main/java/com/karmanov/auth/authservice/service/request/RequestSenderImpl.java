package com.karmanov.auth.authservice.service.request;

import com.karmanov.auth.authservice.exception.request.RequestFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RequestSenderImpl implements RequestSender {
    private static final Logger log = LoggerFactory.getLogger(RequestSenderImpl.class);

    @Override
    public <T> T sendRequest(MediaType contentType, MultiValueMap<String, String> form, String url, Class<T> responseType){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);

        try {
            ResponseEntity<T> response = restTemplate.postForEntity(
                    url,
                    request,
                    responseType
            );

            log.debug("Response: {}",response.getBody());

            if (response.getStatusCode().is2xxSuccessful()){
                return response.getBody();
            }else {
                throw new RequestFailedException("HTTP Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed request\n {}, Message: {}",e.getClass(),e.getMessage(),e);
            throw new RequestFailedException(e.getMessage());
        }
    }
}

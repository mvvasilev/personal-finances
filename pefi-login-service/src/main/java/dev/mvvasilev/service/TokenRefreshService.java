package dev.mvvasilev.service;

import dev.mvvasilev.dto.TokenDTO;
import dev.mvvasilev.exception.PefiLoginServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TokenRefreshService {

    private final ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public TokenRefreshService(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    public TokenDTO fetchNewTokens(String refreshToken) {
        var client = clientRegistrationRepository.findByRegistrationId("authentik");

        var template = new RestTemplate();

        var requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.put("grant_type", List.of("refresh_token"));
        requestBody.put("client_id", List.of(client.getClientId()));
        requestBody.put("client_secret", List.of(client.getClientSecret()));
        requestBody.put("refresh_token", List.of(refreshToken));

        var requestHeaders = new LinkedMultiValueMap<String, String>();
        requestHeaders.put("Content-Type", List.of("application/x-www-form-urlencoded"));

        var request = new HttpEntity<>(requestBody, requestHeaders);

        var tokenResponse = template.postForEntity(client.getProviderDetails().getTokenUri(), request, TokenDTO.class);

        if (!HttpStatus.OK.isSameCodeAs(tokenResponse.getStatusCode())) {
            throw new PefiLoginServiceException("Token refresh failure");
        }

        return tokenResponse.getBody();
    }

}

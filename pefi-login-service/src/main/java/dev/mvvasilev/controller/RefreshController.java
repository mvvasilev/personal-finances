package dev.mvvasilev.controller;

import dev.mvvasilev.configuration.SecurityConfiguration;
import dev.mvvasilev.dto.TokenDTO;
import dev.mvvasilev.exception.PefiLoginServiceException;
import dev.mvvasilev.service.TokenRefreshService;
import dev.mvvasilev.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class RefreshController {

    private final TokenRefreshService tokenRefreshService;

    public RefreshController(TokenRefreshService tokenRefreshService) {
        this.tokenRefreshService = tokenRefreshService;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Void> getOidcUserPrincipal(
            HttpServletResponse response,
            @CookieValue(CookieUtils.REFRESH_TOKEN_NAME) String refreshToken
    ) {
        try {
            var token = tokenRefreshService.fetchNewTokens(refreshToken);

            response.addCookie(CookieUtils.createAccessTokenCookie(token.accessToken()));
            response.addCookie(CookieUtils.createRefreshTokenCookie(token.refreshToken()));

            return ResponseEntity.ok().build();
        } catch (PefiLoginServiceException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}

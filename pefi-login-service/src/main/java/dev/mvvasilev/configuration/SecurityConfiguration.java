package dev.mvvasilev.configuration;

import dev.mvvasilev.service.TokenRefreshService;
import dev.mvvasilev.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Value("${auth.success.redirect}")
    private String redirect;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, OAuth2AuthorizedClientRepository repository) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(HttpMethod.POST, "/refresh-token").permitAll();
                    authorize.anyRequest().authenticated();
                })
                .oauth2Login(l -> l.successHandler((req, res, auth) -> {
                    OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) auth;

                    OAuth2AuthorizedClient authorizedClient = repository.loadAuthorizedClient(
                            oauth.getAuthorizedClientRegistrationId(),
                            auth,
                            req
                    );

                    res.addCookie(
                            CookieUtils.createAccessTokenCookie(authorizedClient.getAccessToken().getTokenValue())
                    );

                    if (authorizedClient.getRefreshToken() != null) {
                        res.addCookie(
                                CookieUtils.createRefreshTokenCookie(authorizedClient.getRefreshToken().getTokenValue())
                        );
                    }

                    res.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
                    res.addHeader("Location", redirect);

                }))
                .build();
    }
}

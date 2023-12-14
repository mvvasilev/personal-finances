package dev.mvvasilev.finances.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

    @Value("${jwt.issuer-url}")
    public String jwtIssuerUrl;

    private static final String[] SWAGGER_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/v2/api-docs/**",
            "/swagger-resources/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, BearerTokenResolver bearerTokenResolver) throws Exception {
        return httpSecurity
                .cors(c -> c.disable()) // won't be needing cors, as the API will be completely hidden behind an api gateway
                .csrf(c -> c.disable())
                .authorizeHttpRequests(c -> {
                    c.requestMatchers(SWAGGER_URLS).permitAll();
                    c.anyRequest().authenticated();
                })
                .oauth2ResourceServer(c -> {
                    c.jwt(Customizer.withDefaults());
                    c.bearerTokenResolver(bearerTokenResolver);
                })
                .build();

    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(jwtIssuerUrl);
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();
        bearerTokenResolver.setBearerTokenHeaderName(HttpHeaders.AUTHORIZATION);
        return bearerTokenResolver;
    }
}

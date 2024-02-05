package dev.mvvasilev.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.web.server.WebSession;

import java.time.Duration;

@Configuration
@EnableWebFluxSecurity
@EnableRedisWebSession
public class SecurityConfiguration implements BeanClassLoaderAware {

    public static final String IS_LOGGED_IN_COOKIE = "isLoggedIn";

    @Value("${spring.security.oauth2.client.provider.authentik.back-channel-logout-url}")
    private String backChannelLogoutUrl;

    @Value("${server.reactive.session.cookie.max-age}")
    private Duration springSessionDuration;

    @Value("${server.reactive.session.cookie.path}")
    private String springSessionPath;

    private ClassLoader loader;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ServerOAuth2AuthorizationRequestResolver resolver) {
        http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(c -> {
                    c.pathMatchers("/**").permitAll();
                    c.pathMatchers("/api/**").authenticated();
                })
                .oauth2Login(c -> {
                    c.authenticationSuccessHandler((exchange, auth) -> {
                        ServerHttpResponse response = exchange.getExchange().getResponse();

                        response.getCookies().set(
                                IS_LOGGED_IN_COOKIE,
                                ResponseCookie.from(IS_LOGGED_IN_COOKIE)
                                        .value("true")
                                        .path(springSessionPath)
                                        .maxAge(springSessionDuration)
                                        .httpOnly(false)
                                        .secure(false)
                                        .build()
                        );

                        return new RedirectServerAuthenticationSuccessHandler("/").onAuthenticationSuccess(exchange, auth);
                    });

                    c.authorizationRequestResolver(resolver);
                })
                .logout(c -> {
                    c.logoutSuccessHandler((ex, a) -> {
                        ServerHttpResponse response = ex.getExchange().getResponse();

                        response.setStatusCode(HttpStatus.SEE_OTHER);
                        response.getHeaders().set("Location", backChannelLogoutUrl);
                        response.getCookies().remove("JSESSIONID");
                        response.getCookies().remove("SESSION");
                        response.getCookies().set(
                                IS_LOGGED_IN_COOKIE,
                                ResponseCookie.from(IS_LOGGED_IN_COOKIE)
                                        .value("false")
                                        .path(springSessionPath)
                                        .maxAge(springSessionDuration)
                                        .httpOnly(false)
                                        .secure(false)
                                        .build()
                        );

                        return ex.getExchange().getSession().flatMap(WebSession::invalidate);
                    });
                })
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }

    @Bean
    public ServerOAuth2AuthorizationRequestResolver pkceResolver(ReactiveClientRegistrationRepository repo) {
        var resolver = new DefaultServerOAuth2AuthorizationRequestResolver(repo);
        resolver.setAuthorizationRequestCustomizer(OAuth2AuthorizationRequestCustomizers.withPkce());
        return resolver;
    }

    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(this.loader));
        return mapper;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.loader = classLoader;
    }

}

package dev.mvvasilev.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestCustomizers;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;
import org.springframework.web.server.WebSession;

@Configuration
@EnableWebFluxSecurity
@EnableRedisWebSession
public class SecurityConfiguration implements BeanClassLoaderAware {

    @Value("${spring.security.oauth2.client.provider.authentik.back-channel-logout-url}")
    private String backChannelLogoutUrl;

    private ClassLoader loader;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, ServerOAuth2AuthorizationRequestResolver resolver) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(c -> {
                    c.pathMatchers("/**").permitAll();
                    c.pathMatchers("/api/**").authenticated();
                })
                .oauth2Login(c -> {
                    c.authorizationRequestResolver(resolver);
                })
                .logout(c -> {
                    c.logoutSuccessHandler((ex, a) -> {
                        ServerHttpResponse response = ex.getExchange().getResponse();

                        response.setStatusCode(HttpStatus.SEE_OTHER);
                        response.getHeaders().set("Location", backChannelLogoutUrl);
                        response.getCookies().remove("JSESSIONID");

                        return ex.getExchange().getSession().flatMap(WebSession::invalidate);
                    });
                });

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

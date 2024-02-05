package dev.mvvasilev.finances.configuration;

import dev.mvvasilev.common.configuration.CommonSecurityConfiguration;
import dev.mvvasilev.common.configuration.CommonSwaggerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
@Import(CommonSecurityConfiguration.class)
public class SecurityConfiguration {
}

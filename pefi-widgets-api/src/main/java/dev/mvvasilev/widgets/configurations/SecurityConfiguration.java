package dev.mvvasilev.widgets.configurations;

import dev.mvvasilev.common.configuration.CommonSecurityConfiguration;
import dev.mvvasilev.common.configuration.CommonSwaggerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(CommonSecurityConfiguration.class)
@EnableTransactionManagement
public class SecurityConfiguration {
}

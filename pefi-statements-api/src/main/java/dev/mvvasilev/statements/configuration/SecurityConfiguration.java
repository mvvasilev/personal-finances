package dev.mvvasilev.statements.configuration;

import dev.mvvasilev.common.configuration.CommonSwaggerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(CommonSwaggerConfiguration.class)
@EnableTransactionManagement
public class SecurityConfiguration {
}

package dev.mvvasilev.finances.configuration;

import dev.mvvasilev.common.configuration.CommonSwaggerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonSwaggerConfiguration.class)
public class SwaggerConfiguration {

}

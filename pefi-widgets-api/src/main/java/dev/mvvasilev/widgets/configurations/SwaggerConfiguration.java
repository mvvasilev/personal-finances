package dev.mvvasilev.widgets.configurations;

import dev.mvvasilev.common.configuration.CommonSwaggerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CommonSwaggerConfiguration.class)
public class SwaggerConfiguration {

}

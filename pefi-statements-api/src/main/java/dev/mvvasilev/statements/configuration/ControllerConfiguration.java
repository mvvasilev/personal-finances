package dev.mvvasilev.statements.configuration;

import dev.mvvasilev.common.configuration.CommonControllerConfiguration;
import org.springframework.context.annotation.Import;

@Import(CommonControllerConfiguration.class)
public class ControllerConfiguration {
}

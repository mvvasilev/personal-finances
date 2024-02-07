package dev.mvvasilev.widgets;

import dev.mvvasilev.common.services.AuthorizationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EntityScan("dev.mvvasilev.widgets.*")
@EnableJpaRepositories("dev.mvvasilev.widgets.*")
@SpringBootApplication(
        scanBasePackageClasses = { AuthorizationService.class },
        scanBasePackages = "dev.mvvasilev.widgets.*"
)
public class PefiWidgetsAPI {
    public static void main(String[] args) {
        SpringApplication.run(PefiWidgetsAPI.class, args);
    }
}
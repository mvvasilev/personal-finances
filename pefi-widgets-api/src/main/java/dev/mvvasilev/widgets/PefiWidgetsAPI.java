package dev.mvvasilev.widgets;

import dev.mvvasilev.common.services.AuthorizationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackageClasses = { AuthorizationService.class })
@EnableJpaRepositories("dev.mvvasilev.widgets.*")
@EntityScan("dev.mvvasilev.widgets.*")
public class PefiWidgetsAPI {
    public static void main(String[] args) {
        SpringApplication.run(PefiWidgetsAPI.class, args);
    }
}
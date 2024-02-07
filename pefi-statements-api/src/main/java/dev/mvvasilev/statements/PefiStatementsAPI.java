package dev.mvvasilev.statements;

import dev.mvvasilev.common.services.AuthorizationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@EntityScan("dev.mvvasilev.statements.*")
@EnableJpaRepositories("dev.mvvasilev.statements.*")
@SpringBootApplication(
        scanBasePackageClasses = { AuthorizationService.class },
        scanBasePackages = "dev.mvvasilev.statements.*"
)
public class PefiStatementsAPI {
    public static void main(String[] args) {
        SpringApplication.run(PefiStatementsAPI.class, args);
    }
}
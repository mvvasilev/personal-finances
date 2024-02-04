package dev.mvvasilev.statements;

import dev.mvvasilev.common.services.AuthorizationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackageClasses = { AuthorizationService.class })
@EnableJpaRepositories("dev.mvvasilev.statements.*")
@EntityScan("dev.mvvasilev.statements.*")
public class PefiStatementsAPI {
    public static void main(String[] args) {
        SpringApplication.run(PefiStatementsAPI.class, args);
    }
}
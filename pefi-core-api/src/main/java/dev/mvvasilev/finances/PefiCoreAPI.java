package dev.mvvasilev.finances;

import dev.mvvasilev.common.services.AuthorizationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackageClasses = { AuthorizationService.class })
@EnableJpaRepositories("dev.mvvasilev.finances.*")
@EntityScan("dev.mvvasilev.finances.*")
public class PefiCoreAPI {

	public static void main(String[] args) {
		SpringApplication.run(PefiCoreAPI.class, args);
	}

}

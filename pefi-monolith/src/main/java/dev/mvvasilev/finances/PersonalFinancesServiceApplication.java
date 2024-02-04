package dev.mvvasilev.finances;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("dev.mvvasilev.finances.*")
@EntityScan("dev.mvvasilev.finances.*")
public class PersonalFinancesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalFinancesServiceApplication.class, args);
	}

}

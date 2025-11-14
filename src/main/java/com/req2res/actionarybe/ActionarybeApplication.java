package com.req2res.actionarybe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ActionarybeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActionarybeApplication.class, args);
	}

}

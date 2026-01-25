package com.req2res.actionarybe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableJpaAuditing
@SpringBootApplication
@EnableScheduling
public class ActionarybeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActionarybeApplication.class, args);
	}

}

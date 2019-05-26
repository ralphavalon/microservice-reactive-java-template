package com.example.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableMongoAuditing
public class TemplateApplication {

	@Bean
	WebClient client() {
		return WebClient.builder().build();
	}

	public static void main(String[] args) {
		SpringApplication.run(TemplateApplication.class, args);
	}

}

package com.example.template.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.template.client.ProviderClient;
import com.example.template.model.Template;

import reactor.core.publisher.Mono;

@Service
public class TemplateClientService implements ProviderClient {
	
	@Value("${provider.url}")
	private String url;
	
	@Autowired
	private WebClient webClient;
	
	@Override
	public Mono<String> callProvider(Template template) {
		return webClient
			.post()
				.uri(url)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(Mono.just(template), Template.class)
			.retrieve()
				.bodyToMono(String.class)
				.retry(2L)
				.log();
	}

}

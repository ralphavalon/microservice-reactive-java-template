package com.example.template.client;

import com.example.template.model.Template;

import reactor.core.publisher.Mono;

public interface ProviderClient {

	Mono<String> callProvider(Template template);

}

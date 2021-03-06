package com.example.template.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.template.client.ProviderClient;
import com.example.template.exception.NotFoundException;
import com.example.template.model.Template;
import com.example.template.repository.TemplateRepository;

import reactor.core.publisher.Mono;

@Service
public class TemplateService {
	
	@Autowired
	private TemplateRepository templateRepository;
	@Autowired
	private ProviderClient providerClient;

	public Mono<Template> save(Template template) {
		return providerClient.callProvider(template)
			.map(t -> template)
			.flatMap(templateRepository::save);
	}
	
	public Mono<Template> get(String templateId) {
		return templateRepository.findById(templateId)
				.switchIfEmpty(Mono.error(new NotFoundException(templateId)));
	}
	
}

package com.example.template.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.template.controller.request.TemplateCreateRequest;
import com.example.template.controller.response.TemplateCreateResponse;
import com.example.template.controller.response.TemplateResponse;
import com.example.template.service.TemplateService;

import reactor.core.publisher.Mono;

@RestController
public class TemplateController {
	
	@Autowired
	private TemplateService templateService;
	
	@GetMapping(value = "/templates/{template_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code = HttpStatus.OK)
	public Mono<TemplateResponse> getTemplate(@PathVariable(name="template_id") String templateId) {
		return Mono.from(
				templateService.get(templateId)
					.map(template -> new TemplateResponse(template))
				);
	}

	@PostMapping(value = "/templates", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseStatus(code = HttpStatus.CREATED)
	public Mono<TemplateCreateResponse> createTemplate(@RequestBody @Valid Mono<TemplateCreateRequest> request) {
		return Mono.from(
					request
					.flatMap(templateRequest -> templateService.save(templateRequest.toModel())
					.map(savedTemplate -> new TemplateCreateResponse(savedTemplate)))
				);
	}

}

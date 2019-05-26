package com.example.template.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.template.controller.handler.GlobalExceptionHandler;
import com.example.template.exception.NotFoundException;
import com.example.template.helper.JsonHelper;
import com.example.template.model.Template;
import com.example.template.service.TemplateService;

import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@WebFluxTest
@ContextConfiguration(classes = {
    GlobalExceptionHandler.class,
    TemplateController.class
})
public class TemplateControllerTest {

	@Autowired
	private WebTestClient webClient;
	
	@MockBean
	private TemplateService templateService;
	
	private Template template;

	@BeforeEach
	public void setUp() {
		template = new Template();
		template.setId("6e71d0d568e134c029203593b00a0103e7cdf30b");
		template.setField("field");
		template.setImmutableField("immutableField");
		template.setCreatedAt(LocalDateTime.now());
		template.setUpdatedAt(LocalDateTime.now());
		doReturn(Mono.just(template)).when(templateService).save(any(Template.class));
		doReturn(Mono.just(template)).when(templateService).get(eq("6e71d0d568e134c029203593b00a0103e7cdf30b"));
		doThrow(new NotFoundException("unexisting_template")).when(templateService).get(eq("unexisting_template"));
	}

	@Test
	public void shouldCreateTemplate() throws Exception {
		String request = JsonHelper.getRequestFileAsString("template/create_template_success.json");
		String response = JsonHelper.getResponseFileAsString("template/create_template_success.json");

		webClient.post().uri("/templates")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.body(Mono.just(request), String.class)
				.exchange()
				.expectStatus().isCreated()
				.expectBody()
				.json(response);

		verify(templateService, times(1)).save(any(Template.class));
	}

	@Test
	public void shouldGetOneTemplate() throws Exception {
		String response = JsonHelper.getResponseFileAsString("template/get_one_template_success.json");

		webClient.get().uri("/templates/{template_id}", "6e71d0d568e134c029203593b00a0103e7cdf30b")
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.json(response);

		verify(templateService, times(1)).get("6e71d0d568e134c029203593b00a0103e7cdf30b");
	}

	@Test
	public void shouldThrowErrorWhenGetUnexistingTemplate() throws Exception {
		String response = JsonHelper.getResponseFileAsString("template/get_one_template_not_found.json");

		webClient.get().uri("/templates/{template_id}", "unexisting_template")
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.json(response);

		verify(templateService, times(1)).get("unexisting_template");
	}

}
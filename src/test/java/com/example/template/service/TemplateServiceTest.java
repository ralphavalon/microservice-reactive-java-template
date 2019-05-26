package com.example.template.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.template.client.ProviderClient;
import com.example.template.exception.NotFoundException;
import com.example.template.model.Template;
import com.example.template.repository.TemplateRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TemplateService.class })
public class TemplateServiceTest {

	@MockBean
	private TemplateRepository templateRepository;
	@MockBean
	private ProviderClient providerClient;

	@Autowired
	private TemplateService templateService;
	
	@Test
	public void shouldSaveTemplate() {
		doReturn(Mono.just("response")).when(providerClient).callProvider(any(Template.class));
		doReturn(Mono.just(new Template())).when(templateRepository).save(any(Template.class));

		StepVerifier
			.create(templateService.save(new Template()))
			.expectNextCount(1L)
			.verifyComplete();
		
		verify(providerClient, times(1)).callProvider(any(Template.class));
		verify(templateRepository, times(1)).save(any(Template.class));
	}

	@Test
	public void shouldGetOneTemplate() throws Exception {
		doReturn(Mono.just(new Template())).when(templateRepository).findById(anyString());
		
		StepVerifier
			.create(templateService.get("123"))
			.expectNextCount(1L)
			.verifyComplete();

		verify(templateRepository, times(1)).findById(anyString());
	}

	@Test
	public void shouldThrowErrorWhenGetUnexistingTemplate() throws Exception {
		doReturn(Mono.empty()).when(templateRepository).findById(eq("unexisting_template"));
		
		StepVerifier
			.create(templateService.get("unexisting_template"))
			.verifyError(NotFoundException.class);
	}

}
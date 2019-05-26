package com.example.template.service;

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

	@Autowired
	private TemplateService templateService;

	@Test
	public void shouldGetOneTemplate() throws Exception {
		doReturn(Mono.just(new Template())).when(templateRepository).findById(anyString());
		
		StepVerifier
			.create(templateService.get("123"))
			.expectNextCount(1L)
			.expectComplete();

		verify(templateRepository, times(1)).findById(anyString());
	}

	@Test
	public void shouldThrowErrorWhenGetUnexistingTemplate() throws Exception {
		doReturn(Mono.empty()).when(templateRepository).findById(eq("unexisting_template"));
		
		StepVerifier
			.create(templateService.get("unexisting_template"))
			.expectError(NotFoundException.class);
	}

}
package com.example.template.client.service;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.template.client.ProviderClient;
import com.example.template.model.Template;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
    "provider.url=http://localhost:9999",
})
public class TemplateClientServiceTest {

	@Autowired
	private ProviderClient providerClient;

	@Autowired
	private ObjectMapper objectMapper;
	
	private final MockWebServer mockWebServer = new MockWebServer();
	
	@BeforeEach
	void setUp() throws IOException {
		mockWebServer.start(9999);
	}

	@AfterEach
	void tearDown() throws IOException {
		mockWebServer.shutdown();
	}

	@Test
	public void shouldCallProvider() throws Exception {
		Template template = new Template();
		template.setField("test_field");
		template.setImmutableField("test_immutableField");

		String jsonBody = objectMapper.writeValueAsString(template);
		
		mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .setBody(jsonBody)
        );

		StepVerifier
			.create(providerClient.callProvider(template))
			.expectNext(jsonBody)
			.verifyComplete();
	}

	@Test
	public void shouldRetryWhenError() throws Exception {
		Template template = new Template();
		template.setField("test_field");
		template.setImmutableField("test_immutableField");

		String jsonBody = objectMapper.writeValueAsString(template);

		MockResponse notFoundResponse = new MockResponse()
		        .setResponseCode(HttpStatus.NOT_FOUND.value())
		        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
		        .setBody("{}");

		mockWebServer.enqueue(notFoundResponse);
		mockWebServer.enqueue(notFoundResponse);
		mockWebServer
			.enqueue(
	            new MockResponse()
	                    .setResponseCode(200)
	                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
	                    .setBody(jsonBody)
	        );
		
		StepVerifier
			.create(providerClient.callProvider(template))
			.expectNext(jsonBody)
			.verifyComplete();
		
	}

}
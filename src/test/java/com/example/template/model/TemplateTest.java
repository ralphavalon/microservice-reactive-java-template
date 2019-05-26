package com.example.template.model;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class TemplateTest {
	
	@Test
	public void shouldValidateTemplate() throws Exception {
		EqualsVerifier.forClass(Template.class)
		.withIgnoredFields("createdAt", "updatedAt")
		.report();
	}

}

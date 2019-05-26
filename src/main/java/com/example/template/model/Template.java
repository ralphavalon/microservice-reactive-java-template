package com.example.template.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@EqualsAndHashCode(callSuper=false)
@ToString
@Document
public class Template extends DateAware {

	@Id
	private String id;
	private String field;
	private String immutableField;

}
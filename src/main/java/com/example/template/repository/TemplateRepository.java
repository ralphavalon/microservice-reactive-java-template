package com.example.template.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.template.model.Template;

@Repository
public interface TemplateRepository extends ReactiveCrudRepository<Template, String>{
	
}

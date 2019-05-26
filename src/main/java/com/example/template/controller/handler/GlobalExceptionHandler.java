package com.example.template.controller.handler;

import java.util.List;
import java.util.Objects;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.example.template.exception.NotFoundException;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@Slf4j
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
	
	private static final List<ExceptionRule> EXCEPTION_RULES = List.of(
            new ExceptionRule(NotFoundException.class, GlobalExceptionHandler::notFound)
    );

    public GlobalExceptionHandler(DefaultErrorAttributes g, ApplicationContext applicationContext, 
            ServerCodecConfigurer serverCodecConfigurer) {
        super(g, new ResourceProperties(), applicationContext);
        super.setMessageWriters(serverCodecConfigurer.getWriters());
        super.setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::handleError);
    }

    private Mono<ServerResponse> handleError(ServerRequest serverRequest) {
        Throwable error = getError(serverRequest);

        return EXCEPTION_RULES.stream()
                .map(exceptionRule -> exceptionRule.getResponse(error))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> internalServerError(error));
    }

    private static Mono<ServerResponse> notFound(Throwable error) {
        return ServerResponse
                .status(HttpStatus.NOT_FOUND)
                .body(Mono.just(
            		errorMessage("not found", error.getMessage())
        		), ErrorMessage.class);
    }

    private Mono<ServerResponse> internalServerError(Throwable error) {
        log.error("Server error", error);

        return ServerResponse
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Mono.just(
                		errorMessage("Internal Server Error", "Unexpected server error")
            		), ErrorMessage.class);
    }
    
    private static ErrorMessage errorMessage(String error, String message) {
		return errorMessage(error, message, null);
	}

	private static ErrorMessage errorMessage(String error, String message, Object details) {
		return ErrorMessage.builder().error(error).message(message).details(details).build();
	}
	
	@Getter
	@Builder
	static class ErrorMessage {
		
		@NonNull
		private String error;
		@NonNull
		private String message;
		@JsonInclude(JsonInclude.Include.NON_NULL)
		private Object details;
		
	}

}

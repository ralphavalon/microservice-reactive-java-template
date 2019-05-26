package com.example.template.controller.handler;

import java.util.function.Function;

import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
final class ExceptionRule {

    private Class<?> exceptionClass;
    private Function<Throwable, Mono<ServerResponse>> function;

    private boolean supports(Throwable error) {
        return exceptionClass.isInstance(error);
    }

    Mono<ServerResponse> getResponse(Throwable error) {
        return supports(error) ? function.apply(error) : null;
    }
}

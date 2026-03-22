package com.tcs.orderservice.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(WebExchangeBindException ex) {
        List<String> errors = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        log.warn("Validation error: {}", errors);
        return Mono.just(ResponseEntity.badRequest().body(buildErrorBody(HttpStatus.BAD_REQUEST, errors)));
    }

    @ExceptionHandler(org.springframework.web.server.ServerWebInputException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleBadInput(
            org.springframework.web.server.ServerWebInputException ex) {
        log.warn("Invalid request body: {}", ex.getMessage());
        return Mono.just(ResponseEntity.badRequest()
                .body(buildErrorBody(HttpStatus.BAD_REQUEST, List.of("Invalid request body. Please check the data types sent."))));
    }

    @ExceptionHandler(com.fasterxml.jackson.core.JsonProcessingException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleJsonProcessing(
            com.fasterxml.jackson.core.JsonProcessingException ex) {
        log.error("JSON serialization/deserialization error: {}", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorBody(HttpStatus.INTERNAL_SERVER_ERROR, List.of("Internal error processing the order"))));
    }

    private Map<String, Object> buildErrorBody(HttpStatus status, List<String> errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("messages", errors);
        return body;
    }

}

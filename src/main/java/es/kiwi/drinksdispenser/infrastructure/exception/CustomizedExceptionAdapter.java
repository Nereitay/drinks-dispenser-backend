package es.kiwi.drinksdispenser.infrastructure.exception;

import es.kiwi.drinksdispenser.domain.exception.MachineNotFoundException;
import es.kiwi.drinksdispenser.domain.exception.ProductNotFoundException;
import es.kiwi.drinksdispenser.infrastructure.exception.data.response.ExceptionResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class CustomizedExceptionAdapter {

    @ExceptionHandler(MachineNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Mono<ExceptionResponses>> handleMachineNotFoundException(MachineNotFoundException ex, ServerWebExchange exchange) {
        ExceptionResponses response = new ExceptionResponses(LocalDateTime.now(), ex.getMessage(),
                Collections.singletonList(exchange.getRequest().getURI().toString()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Mono.just(response));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Mono<ExceptionResponses>> handleProductNotFoundException(ProductNotFoundException ex, ServerWebExchange exchange) {
        ExceptionResponses response = new ExceptionResponses(LocalDateTime.now(), ex.getMessage(),
                Collections.singletonList(exchange.getRequest().getURI().toString()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Mono.just(response));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Mono<ExceptionResponses>> handleResponseStatusException(ResponseStatusException ex, ServerWebExchange exchange) {
        ExceptionResponses response = new ExceptionResponses(LocalDateTime.now(), ex.getMessage(),
                Collections.singletonList(exchange.getRequest().getURI().toString()));
        return ResponseEntity.status(ex.getStatus()).body(Mono.just(response));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Mono<ExceptionResponses>> handleException(Exception ex, ServerWebExchange exchange) {
        ExceptionResponses response = new ExceptionResponses(LocalDateTime.now(), ex.getMessage(),
                Collections.singletonList(exchange.getRequest().getURI().toString()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Mono.just(response));
    }
}

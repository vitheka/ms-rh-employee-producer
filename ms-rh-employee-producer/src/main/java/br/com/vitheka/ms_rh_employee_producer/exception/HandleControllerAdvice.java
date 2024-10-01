package br.com.vitheka.ms_rh_employee_producer.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class HandleControllerAdvice {

    private final Logger log = LoggerFactory.getLogger(HandleControllerAdvice.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleException(MethodArgumentNotValidException ex) {

        var errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError
                        .getField()
                        .concat(" - ").concat(Objects.requireNonNull(fieldError.getDefaultMessage()))
                ).sorted()
                .collect(Collectors.joining(", "));

        log.info("errorMessage: {}", errorMessage);

        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}

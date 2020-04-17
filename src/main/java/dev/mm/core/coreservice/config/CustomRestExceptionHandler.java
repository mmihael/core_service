package dev.mm.core.coreservice.config;

import dev.mm.core.coreservice.dto.validation.ValidationErrorExceptionDto;
import dev.mm.core.coreservice.exception.ValidationErrorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity handleValidationErrorException(ValidationErrorException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ValidationErrorExceptionDto(e));
    }
}
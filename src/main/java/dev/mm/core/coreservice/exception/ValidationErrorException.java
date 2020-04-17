package dev.mm.core.coreservice.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
@ResponseBody
public class ValidationErrorException extends RuntimeException {

    private final String type = "validation-error";
    private String errorMessage;
    private Map<String, List<String>> propErrors = new HashMap<>();

    public ValidationErrorException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ValidationErrorException(String prop1, String prop1Error) {
        this.errorMessage = "Validation error";
        propErrors.put(prop1, Collections.singletonList(prop1Error));
    }
}
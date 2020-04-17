package dev.mm.core.coreservice.dto.validation;

import dev.mm.core.coreservice.exception.ValidationErrorException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ValidationErrorExceptionDto {

    private String type;
    private String errorMessage;
    private Map<String, List<String>> propErrors = new HashMap<>();

    public ValidationErrorExceptionDto(ValidationErrorException e) {
        type = e.getType();
        errorMessage = e.getErrorMessage();
        propErrors = e.getPropErrors();
    }
}

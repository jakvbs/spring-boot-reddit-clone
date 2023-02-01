package pl.jsieczczynski.SpringBootRedditClone.exceptions.config;

import io.jsonwebtoken.JwtException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.jsieczczynski.SpringBootRedditClone.exceptions.AppException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({AppException.class})
    public ResponseEntity<?> handleInvalidRequestException(AppException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<?> handleDataIntegrityException(DataIntegrityViolationException e) {
        return new ResponseEntity<>(
                NestedExceptionUtils.getMostSpecificCause(e).getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler({BindException.class})
    public ResponseEntity<?> handleValidationException(BindException e) {
        List<FieldError> allErrors = e.getFieldErrors();
        Map<String, List<String>> normalizedErrors = new HashMap<>();
        allErrors.forEach(error -> {
            String field = error.getField();
            String defaultMessage = error.getDefaultMessage();
            if (normalizedErrors.containsKey(field)) {
                normalizedErrors.get(field).add(defaultMessage);
            } else {
                List<String> nextFieldErrors = new ArrayList<>();
                nextFieldErrors.add(defaultMessage);
                normalizedErrors.put(field, nextFieldErrors);
            }
        });
        return ResponseEntity.badRequest()
                .body(normalizedErrors);
    }

    @ExceptionHandler({JwtException.class})
    public ResponseEntity<?> handleException2(JwtException e) {
        return new ResponseEntity<>(
                NestedExceptionUtils.getMostSpecificCause(e).getMessage(),
                HttpStatus.UNAUTHORIZED
        );
    }
}

package pl.jsieczczynski.SpringBootRedditClone.exceptions;

import io.jsonwebtoken.JwtException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionsHandler {
    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<?> handleException(DataIntegrityViolationException e) {
        return new ResponseEntity<>(
                NestedExceptionUtils.getMostSpecificCause(e).getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler({JwtException.class})
    public ResponseEntity<?> handleException2(JwtException e) {
        System.out.println("====================================");
        System.out.println(e.getMessage());
        System.out.println("====================================");
        return new ResponseEntity<>(
                NestedExceptionUtils.getMostSpecificCause(e).getMessage(),
                HttpStatus.UNAUTHORIZED
        );
    }
}

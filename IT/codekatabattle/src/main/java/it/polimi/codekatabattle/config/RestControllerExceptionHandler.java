package it.polimi.codekatabattle.config;

import it.polimi.codekatabattle.exceptions.OAuthException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.xml.bind.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public IOException handleIOException(IOException ex) {
        return ex;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MissingServletRequestParameterException handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return ex;
    }

    @ExceptionHandler(OAuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public OAuthException handleOAuthException(OAuthException ex) {
        return ex;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationException handleValidationException(ValidationException ex) {
        return ex;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public EntityNotFoundException handleEntityNotFoundException(EntityNotFoundException ex) {
        return ex;
    }

}

package it.polimi.codekatabattle.config;

import it.polimi.codekatabattle.exceptions.OAuthException;
import it.polimi.codekatabattle.exceptions.ErrorDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Arrays;

@RestControllerAdvice
public class RestControllerExceptionHandler {

    Logger logger = LoggerFactory.getLogger(RestControllerExceptionHandler.class);

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return new ErrorDetails(ex);
    }

    @ExceptionHandler(OAuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDetails handleOAuthException(OAuthException ex) {
        return new ErrorDetails(ex);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleValidationException(ValidationException ex) {
        return new ErrorDetails(ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDetails handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ErrorDetails(ex);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final ErrorDetails handleAllExceptions(Exception ex) {
        logger.error(ex.getMessage());
        ex.printStackTrace();
        return new ErrorDetails(ex);
    }

}

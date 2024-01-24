package it.polimi.codekatabattle.config;

import it.polimi.codekatabattle.exceptions.OAuthException;
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

}

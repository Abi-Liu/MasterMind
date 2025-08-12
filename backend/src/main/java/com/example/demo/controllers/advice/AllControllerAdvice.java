package com.example.demo.controllers.advice;

import com.example.demo.exceptions.GameCompletedException;
import com.example.demo.exceptions.GameNotFoundException;
import com.example.demo.exceptions.InvalidGuessException;
import com.example.demo.models.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(basePackages = {"com.example.demo.controllers"})
public class AllControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GameCompletedException.class)
    public ErrorDTO handleGameCompletedException(GameCompletedException ex) {
        return new ErrorDTO(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GameNotFoundException.class)
    public ErrorDTO handleGameNotFoundException(GameNotFoundException ex) {
        return new ErrorDTO(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidGuessException.class)
    public ErrorDTO handleInvalidGuessException(InvalidGuessException ex) {
        return new ErrorDTO(ex.getMessage());
    }
}

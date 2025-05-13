package com.example.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    public static final String ERROR = "error";

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException e, Model model) {
        logger.error("Illegal argument: {}", e.getMessage());
        model.addAttribute(ERROR, "Invalid input: " + e.getMessage());
        return ERROR;
    }

    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException e, Model model) {
        logger.error("IO error: {}", e.getMessage());
        model.addAttribute(ERROR, "File processing error: " + e.getMessage());
        return ERROR;
    }

    @ExceptionHandler(DateTimeParseException.class)
    public String handleDateParseException(DateTimeParseException e, Model model) {
        logger.error("Date parsing error: {}", e.getMessage());
        model.addAttribute(ERROR, "Invalid date format: " + e.getParsedString());
        return ERROR;
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        logger.error("Unexpected error occurred", e);
        model.addAttribute(ERROR, "An unexpected error occurred: " + e.getMessage());
        return ERROR;
    }
}


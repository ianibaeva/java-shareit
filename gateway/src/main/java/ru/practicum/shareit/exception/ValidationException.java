package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(HttpStatus badRequest, String itemIsNotAvailable) {
    }
}

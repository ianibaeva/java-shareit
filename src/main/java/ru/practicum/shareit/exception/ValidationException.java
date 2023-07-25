package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@ResponseBody()
public class ValidationException extends ResponseStatusException {
    public ValidationException(HttpStatus httpStatus, String e) {
        super(httpStatus, e);
    }
}

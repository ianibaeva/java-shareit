package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    public void badRequestExceptionsTest() {
        RuntimeException e = new RuntimeException("Bad Request");
        ErrorResponse response = errorHandler.handleBadRequestExceptions(e);
        assertEquals("Bad Request", response.getError());
    }

    @Test
    public void notFoundExceptionsTest() {
        ObjectNotFoundException e = new ObjectNotFoundException("Not Found");
        ErrorResponse response = errorHandler.handleNotFoundExceptions(e);
        assertEquals("Not Found", response.getError());
    }

    @Test
    public void conflictExceptionsTest() {
        ConflictException e = new ConflictException("Conflict");
        ErrorResponse response = errorHandler.handleConflictExceptions(e);
        assertEquals("Conflict", response.getError());
    }

    @Test
    public void forbiddenExceptionsTest() {
        ForbiddenException e = new ForbiddenException("Forbidden");
        ErrorResponse response = errorHandler.handleForbiddenExceptions(e);
        assertEquals("Forbidden", response.getError());
    }

    @Test
    public void otherExceptionsTest() {
        RuntimeException e = new RuntimeException("Internal Server Error");
        ErrorResponse response = errorHandler.handleOtherExceptions(e);
        assertEquals("Internal Server Error", response.getError());
    }
}
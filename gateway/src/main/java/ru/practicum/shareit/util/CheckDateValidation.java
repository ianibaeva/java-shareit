package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidation implements ConstraintValidator<DateValidator, BookItemRequestDto> {
    @Override
    public void initialize(DateValidator constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookItemRequestDto bookingShortDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingShortDto.getStart();
        LocalDateTime end = bookingShortDto.getEnd();
        LocalDateTime now = LocalDateTime.now();

        if (start == null || end == null) {
            return false;
        }
        if (start.isBefore(now)) {
            return false;
        }
        return start.isBefore(end);
    }
}
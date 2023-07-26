package ru.practicum.shareit.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

public class Validator {
    public static void validate(Item items) throws ValidationException {
        if (StringUtils.isBlank(items.getName())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Item has no name");
        }
        if (StringUtils.isBlank(items.getDescription())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Item has no description");
        }
        if (items.getAvailable() == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Item is not available");
        }
    }
}

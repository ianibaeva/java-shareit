package ru.practicum.shareit.validator;

import lombok.Generated;
import lombok.experimental.UtilityClass;

@Generated
@UtilityClass
public class PageValidator {
    public void validatePageParameters(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Page parameters must be non-negative");
        }
    }
}

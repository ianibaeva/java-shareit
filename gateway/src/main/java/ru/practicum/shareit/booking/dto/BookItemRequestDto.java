package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.DateValidator;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@DateValidator
public class BookItemRequestDto {

    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}
package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validator.StartBeforeEndDateValid;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@StartBeforeEndDateValid(groups = {Create.class})
public class BookItemRequestDto {

    private Long itemId;

    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;

    @Future(groups = {Create.class})
    private LocalDateTime end;
}
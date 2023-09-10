package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.util.DateValidator;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@DateValidator
public class BookItemRequestDto {

    private long itemId;

    @FutureOrPresent
    @NotNull
    private LocalDateTime start;

    @Future
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    @NotNull
    private LocalDateTime end;
}
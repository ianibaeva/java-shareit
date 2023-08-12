package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingOutDto {
    public static final String DATE_FORMAT = "yyyy-MM-ddTHH:mm:ss";
    private Long id;

    @NotNull(groups = {Create.class}, message = "Item ID is required")
    @FutureOrPresent
    @DateTimeFormat(pattern = DATE_FORMAT)
    private LocalDateTime start;

    @NotNull(groups = {Create.class})
    @Future
    @DateTimeFormat(pattern = DATE_FORMAT)
    private LocalDateTime end;

    private Status status;

    private User booker;

    private Item item;
}

package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    public static final String DATE_FORMAT = "yyyy-MM-ddTHH:mm:ss";

    @NotNull(groups = {Create.class}, message = "Item ID is required")
    private Long itemId;

    @NotNull(groups = {Create.class})
    @DateTimeFormat(pattern = DATE_FORMAT)
    private LocalDateTime start;

    @NotNull(groups = {Create.class})
    @DateTimeFormat(pattern = DATE_FORMAT)
    private LocalDateTime end;
}

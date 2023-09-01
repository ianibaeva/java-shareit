package ru.practicum.shareit.booking.dto;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.enums.Status;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonTest
class BookingOutDtoTest {

    @Autowired
    private JacksonTester<BookingOutDto> json;

    private static final String TIME_PATTERN_TEST = "yyyy-MM-dd'T'HH:mm:ss.SSSSS";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_PATTERN_TEST);

    @Test
    void bookingOutDtoToJsonTest() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        BookingOutDto.User user = new BookingOutDto.User(1L, "User");
        BookingOutDto.Item item = new BookingOutDto.Item(2L, "Дрель");

        BookingOutDto bookingOutDto = new BookingOutDto(
                1L,
                now.plusDays(1),
                now.plusDays(2),
                Status.WAITING,
                user,
                item
        );

        JsonContent<BookingOutDto> result = json.write(bookingOutDto);

        Assertions.assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        Assertions.assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo((now.plusDays(1)).format(formatter));
        Assertions.assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo((now.plusDays(2)).format(formatter));
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("User");
        Assertions.assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        Assertions.assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Дрель");
    }
}


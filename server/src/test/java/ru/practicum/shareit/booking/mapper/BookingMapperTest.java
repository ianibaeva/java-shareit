package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class BookingMapperTest {

    private BookItemRequestDto bookingDto;
    private Item item;
    private User user;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingDto = new BookItemRequestDto();
        item = mock(Item.class);
        user = mock(User.class);
        booking = new Booking(null, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, user, Status.WAITING);
    }

    @Test
    void testToBooking() {
        Booking result = BookingMapper.toBooking(bookingDto, item, user);

        assertEquals(bookingDto.getStart(), result.getStart());
        assertEquals(bookingDto.getEnd(), result.getEnd());
        assertEquals(item, result.getItem());
        assertEquals(user, result.getBooker());
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    void testToBookingDtoOut() {
        BookingOutDto bookingOutDto = BookingMapper.toBookingDtoOut(booking);

        assertEquals(booking.getId(), bookingOutDto.getId());
        assertEquals(booking.getStart(), bookingOutDto.getStart());
        assertEquals(booking.getEnd(), bookingOutDto.getEnd());
        assertEquals(booking.getStatus(), bookingOutDto.getStatus());

        assertEquals(booking.getBooker().getId(), bookingOutDto.getBooker().getId());
        assertEquals(booking.getBooker().getName(), bookingOutDto.getBooker().getName());

        assertEquals(booking.getItem().getId(), bookingOutDto.getItem().getId());
        assertEquals(booking.getItem().getName(), bookingOutDto.getItem().getName());
    }

    @Test
    void testToBookingItemDto() {
        BookingItemDto bookingItemDto = BookingMapper.toBookingItemDto(booking);

        assertEquals(booking.getId(), bookingItemDto.getId());
        assertEquals(booking.getBooker().getId(), bookingItemDto.getBookerId());
    }
}

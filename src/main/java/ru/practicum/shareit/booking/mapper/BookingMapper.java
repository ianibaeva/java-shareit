package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;

@UtilityClass
public class BookingMapper {
    public static Booking toBooking(@Valid BookItemRequestDto bookingDto, Item item, User user) {
        return new Booking(
                null,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                Status.WAITING
        );
    }

    public static BookingOutDto toBookingDtoOut(Booking booking) {
        return new BookingOutDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingOutDto.User(booking.getBooker().getId(), booking.getBooker().getName()),
                new BookingOutDto.Item(booking.getItem().getId(), booking.getItem().getName())
        );
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }
}



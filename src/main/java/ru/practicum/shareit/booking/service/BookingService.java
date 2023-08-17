package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

public interface BookingService {
    BookingOutDto create(Long userId, BookItemRequestDto bookingDto);

    BookingOutDto update(Long userId, Long bookingId, Boolean approved);

    BookingOutDto getById(Long userId, Long bookingId);

    List<BookingOutDto> getAllByBooker(Long userId, String state);

    List<BookingOutDto> getAllByOwner(Long userId, String state);
}

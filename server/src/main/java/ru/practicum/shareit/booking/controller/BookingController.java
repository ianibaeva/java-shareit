package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.util.Constant;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto createBooking(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @RequestBody BookItemRequestDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto update(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @RequestParam("approved") Boolean approved,
            @PathVariable("bookingId") Long bookingId) {

        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getById(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @PathVariable("bookingId") Long bookingId) {

        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingOutDto> getAll(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        State bookingState = State.from(state);
        if (Objects.isNull(bookingState)) {
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
        return bookingService.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllByOwner(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        State bookingState = State.from(state);
        if (Objects.isNull(bookingState)) {
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
        return bookingService.getAllByOwner(userId, state, from, size);
    }
}

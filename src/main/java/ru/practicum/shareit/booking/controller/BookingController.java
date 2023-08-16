package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.util.Constant;
import ru.practicum.shareit.util.Create;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto createBooking(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @Validated({Create.class})
            @RequestBody BookingDto bookingDto) {
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
            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        State.from(state);
        return bookingService.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllByOwner(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        State.from(state);
        return bookingService.getAllByOwner(userId, state);
    }
}
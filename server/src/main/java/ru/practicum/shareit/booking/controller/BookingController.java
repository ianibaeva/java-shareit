package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Constant;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
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
        return bookingService.getAllByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllByOwner(
            @RequestHeader((Constant.REQUEST_HEADER_USER_ID)) Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.getAllByOwner(userId, state, from, size);
    }
}

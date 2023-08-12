package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDtoOut;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingOutDto create(Long userId, BookingDto bookingDto) {
        User user = toUser(userService.getUserById(userId));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> {
                    log.debug("Item with ID {} not found", bookingDto.getItemId());
                    return new ObjectNotFoundException(String.format("Item with ID %s not found", bookingDto.getItemId()));
                });

        bookingValidation(bookingDto, user, item);

        Booking booking = toBooking(bookingDto, item, user);

        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingOutDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.debug("Booking with ID {} not found", bookingId);
                    return new ObjectNotFoundException(String.format("Booking with ID %s not found", bookingId));
                });

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.debug("User with ID {} is not the owner of item with ID {}", userId, booking.getItem().getId());
            throw new ObjectNotFoundException(String.format("User with ID %s is not the owner of item with ID %s",
                    userId, booking.getItem().getId()));
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            log.debug("Status of the booking with ID {} has already been updated", booking.getId());
            throw new ValidationException(String.format("Status of the booking with ID %s has already been updated",
                    booking.getId()));
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return toBookingDtoOut(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingOutDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.debug("Booking with ID {} not found", bookingId);
                    return new ObjectNotFoundException(String.format("Booking with ID %s not found", bookingId));
                });

        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            log.debug("User with ID {} is not the owner or booker", userId);
            throw new ObjectNotFoundException(String.format("User with ID %s is not the owner or booker", userId));
        }

        return toBookingDtoOut(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutDto> getAllByBooker(Long userId, String state) {
        userService.getUserById(userId);

        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByBookerId(userId).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByBookerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByBookerId(userId).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutDto> getAllByOwner(Long userId, String state) {
        userService.getUserById(userId);

        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByOwnerId(userId).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllCurrentBookingsByOwnerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());

            case PAST:
                return bookingRepository.findAllPastBookingsByOwnerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());

            case FUTURE:
                return bookingRepository.findAllFutureBookingsByOwnerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());

            case WAITING:
                return bookingRepository.findAllWaitingBookingsByOwnerId(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());

            case REJECTED:
                return bookingRepository.findAllRejectedBookingsByOwnerId(userId).stream()
                        .map(BookingMapper::toBookingDtoOut)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void bookingValidation(BookingDto bookingDto, User user, Item item) {
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException(
                    String.format("Start date: %s cannot be before the current time",
                            bookingDto.getStart()));
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException(
                    String.format("End date: %s cannot be before start date: %s",
                            bookingDto.getEnd(), bookingDto.getStart()));
        }
        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException(
                    String.format("End date: %s cannot be equal to start date: %s",
                            bookingDto.getEnd(), bookingDto.getStart()));
        }
        if (!item.getAvailable()) {
            throw new ValidationException(
                    String.format("Item with ID %s not available.",
                            item.getId())
            );
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new ObjectNotFoundException(
                    String.format("Item with ID %s not found",
                            item.getId())
            );
        }
    }
}

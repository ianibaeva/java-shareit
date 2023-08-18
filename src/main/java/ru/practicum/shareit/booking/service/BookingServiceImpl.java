package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
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
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDtoOut;
import static ru.practicum.shareit.enums.State.from;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingOutDto create(Long userId, BookItemRequestDto bookingDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Item with ID %s not found", bookingDto.getItemId())));

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

        Booking booking = toBooking(bookingDto, item, user);

        return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
    }


    @Override
    @Transactional
    public BookingOutDto update(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Booking with ID %s not found", bookingId)));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException(String.format("User with ID %s is not the owner of item with ID %s",
                    userId, booking.getItem().getId()));
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
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
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Booking with ID %s not found", bookingId)));

        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException(String.format("User with ID %s is not the owner or booker", userId));
        }

        return toBookingDtoOut(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutDto> getAllByBooker(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        State bookingState = from(state);
        switch (bookingState) {
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
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        State bookingState = from(state);
        switch (bookingState) {
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
}

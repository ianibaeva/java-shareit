package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public List<BookingOutDto> getAllByBooker(Long userId, String state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        State bookingState = from(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentBookingsByBookerId(userId, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllPastBookingsByBookerId(userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureBookingsByBookerId(userId, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllWaitingBookingsByBookerId(userId, now, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllRejectedBookingsByBookerId(userId, pageable);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + bookingState);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDtoOut)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutDto> getAllByOwner(Long userId, String state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        State bookingState = from(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(userId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllCurrentBookingsByOwnerId(userId, now, pageable);
                break;
            case PAST:
                bookings = bookingRepository.findAllPastBookingsByOwnerId(userId, now, pageable);
                break;
            case FUTURE:
                bookings = bookingRepository.findAllFutureBookingsByOwnerId(userId, now, pageable);
                break;
            case WAITING:
                bookings = bookingRepository.findAllWaitingBookingsByOwnerId(userId, now, pageable);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllRejectedBookingsByOwnerId(userId, pageable);
                break;
            default:
                throw new IllegalArgumentException("Unknown state: " + bookingState);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDtoOut)
                .collect(Collectors.toList());
    }
}

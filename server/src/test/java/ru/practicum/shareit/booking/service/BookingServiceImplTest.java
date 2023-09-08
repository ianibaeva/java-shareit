package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDtoOut;


@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User owner;
    private UserDto userDto;
    private Item item;
    private Booking booking;
    private Booking bookingWaiting;
    private BookItemRequestDto bookingDto;
    private BookItemRequestDto bookingDtoStartBeforeNow;
    private BookItemRequestDto bookingDtoEndBeforeStart;
    private BookItemRequestDto bookingDtoEndEqualsStart;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("username");
        user.setEmail("email@email.com");

        owner = new User();
        owner.setId(2L);
        owner.setName("username2");
        owner.setEmail("email2@email.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("username");
        userDto.setEmail("email@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("item name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1L));
        booking.setEnd(LocalDateTime.now().plusDays(2L));
        booking.setStatus(Status.APPROVED);
        booking.setItem(item);
        booking.setBooker(user);

        bookingWaiting = new Booking();
        bookingWaiting.setId(1L);
        bookingWaiting.setStart(LocalDateTime.now().plusDays(1L));
        bookingWaiting.setEnd(LocalDateTime.now().plusDays(2L));
        bookingWaiting.setStatus(Status.WAITING);
        bookingWaiting.setItem(item);
        bookingWaiting.setBooker(user);

        bookingDto = new BookItemRequestDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1L));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2L));

        bookingDtoStartBeforeNow = new BookItemRequestDto();
        bookingDtoStartBeforeNow.setItemId(1L);
        bookingDtoStartBeforeNow.setStart(LocalDateTime.now().minusDays(1L));
        bookingDtoStartBeforeNow.setEnd(LocalDateTime.now().plusDays(2L));

        bookingDtoEndBeforeStart = new BookItemRequestDto();
        bookingDtoEndBeforeStart.setItemId(1L);
        bookingDtoEndBeforeStart.setStart(LocalDateTime.now().plusDays(1L));
        bookingDtoEndBeforeStart.setEnd(LocalDateTime.now().minusDays(1L));

        bookingDtoEndEqualsStart = new BookItemRequestDto();
        bookingDtoEndEqualsStart.setItemId(1L);
        bookingDtoEndEqualsStart.setStart(LocalDateTime.now().plusMinutes(1L));
        bookingDtoEndEqualsStart.setEnd(LocalDateTime.now().plusMinutes(1L));
    }

    @Test
    void testCreateBooking() {
        Long userId = userDto.getId();

        BookingOutDto expectedBookingDtoOut = toBookingDtoOut(toBooking(bookingDto, item, user));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.save(any(Booking.class))).thenReturn(toBooking(bookingDto, item, user));

        BookingOutDto actualBookingDtoOut = bookingService.create(userDto.getId(), bookingDto);

        assertEquals(expectedBookingDtoOut, actualBookingDtoOut);
    }

    @Test
    void testCreateBooking_UserIsNotFound() {
        Long userId = userDto.getId();
        BookItemRequestDto bookingDto = new BookItemRequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> bookingService.create(userId, bookingDto));

        verifyNoInteractions(itemRepository, bookingRepository);
    }

    @Test
    void testCreateBooking_ItemIsNotAvailable() {
        Long userId = userDto.getId();
        item.setAvailable(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException bookingValidationException = assertThrows(ValidationException.class,
                () -> bookingService.create(userDto.getId(), bookingDto));

        assertEquals(bookingValidationException.getMessage(), String.format("Item with ID %s not available.",
                item.getId()));
    }

    @Test
    void testCreateBooking_ItemIsNotFound() {
        Long userId = userDto.getId();
        item.setOwner(user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ObjectNotFoundException bookingNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(userDto.getId(), bookingDto));

        assertEquals(bookingNotFoundException.getMessage(), String.format("Item with ID %s not found",
                item.getId()));

    }

    @Test
    void update() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingOutDto actualBookingDtoOut = bookingService.update(owner.getId(), bookingWaiting.getId(), true);

        assertEquals(Status.APPROVED, actualBookingDtoOut.getStatus());
    }

    @Test
    void update_whenStatusIsNotApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(bookingWaiting));
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingWaiting);

        BookingOutDto actualBookingDtoOut = bookingService.update(owner.getId(), bookingWaiting.getId(), false);

        assertEquals(Status.REJECTED, actualBookingDtoOut.getStatus());
    }

    @Test
    void update_whenStatusIsNotWaiting() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ValidationException bookingValidationException = assertThrows(ValidationException.class,
                () -> bookingService.update(owner.getId(), booking.getId(), false));

        assertEquals(bookingValidationException.getMessage(), String.format("Status of the booking with ID %s has already been updated",
                booking.getId()));
    }

    @Test
    void update_whenUserIsNotTheItemOwner_ThrowsNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ObjectNotFoundException bookingNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.update(userDto.getId(), booking.getId(), true));

        assertEquals(bookingNotFoundException.getMessage(), String.format("User with ID %s is not the owner of item with ID %s",
                userDto.getId(), booking.getItem().getId()));
    }

    @Test
    void getById() {
        BookingOutDto expectedBookingDtoOut = toBookingDtoOut(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingOutDto actualBookingDtoOut = bookingService.getById(user.getId(), booking.getId());

        assertEquals(expectedBookingDtoOut, actualBookingDtoOut);
    }

    @Test
    void getById_whenBookingIdIsNotValid_ThrowsObjectNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        ObjectNotFoundException bookingNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getById(1L, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), String.format("Booking with ID %s not found", booking.getId()));
    }

    @Test
    void getById_whenUserIsNotTheItemOwner_ThrowsObjectNotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ObjectNotFoundException bookingNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getById(3L, booking.getId()));

        assertEquals(bookingNotFoundException.getMessage(), String.format("User with ID %s is not the owner or booker", 3L));
    }

    @Test
    void getAllByBooker_whenBookingStateIsAll() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByBooker(user.getId(), "ALL", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBooker_whenBookingStateIsCURRENT() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllCurrentBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByBooker(user.getId(), "CURRENT", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBooker_whenBookingStateIsPAST() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllPastBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByBooker(user.getId(), "PAST", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBooker_whenBookingStateIsFUTURE() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllFutureBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByBooker(user.getId(), "FUTURE", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBooker_whenBookingStateIsWAITING() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllWaitingBookingsByBookerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByBooker(user.getId(), "WAITING", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByBooker_whenBookingStateIsREJECTED() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllRejectedBookingsByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByBooker(user.getId(), "REJECTED", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

//    @Test
//    void getAllByBooker_whenBookingStateIsNotValid_ThrowsIllegalArgumentException() {
//        Long userId = userDto.getId();
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        assertThrows(IllegalArgumentException.class,
//                () -> bookingService.getAllByBooker(user.getId(), "INVALID_STATE", 0, 10));
//    }

    @Test
    void getAllByOwner_whenBookingStateIsAll() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByOwnerId(anyLong(), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByOwner(user.getId(), "ALL", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwner_whenBookingStateIsCURRENT() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllCurrentBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByOwner(user.getId(), "CURRENT", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwner_whenBookingStateIsPAST() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllPastBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByOwner(user.getId(), "PAST", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwner_whenBookingStateIsFUTURE() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllFutureBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByOwner(user.getId(), "FUTURE", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwner_whenBookingStateIsWAITING() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllWaitingBookingsByOwnerId(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByOwner(user.getId(), "WAITING", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

    @Test
    void getAllByOwner_whenBookingStateIsREJECTED() {
        Long userId = userDto.getId();
        List<BookingOutDto> expectedBookingsDtoOut = List.of(toBookingDtoOut(booking));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllRejectedBookingsByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));

        List<BookingOutDto> actualBookingsDtoOut = bookingService.getAllByOwner(user.getId(), "REJECTED", 0, 10);

        assertEquals(expectedBookingsDtoOut, actualBookingsDtoOut);
    }

//    @Test
//    void getAllByOwner_whenBookingStateIsNotValid_ThrowsIllegalArgumentException() {
//        Long userId = userDto.getId();
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        assertThrows(IllegalArgumentException.class,
//                () -> bookingService.getAllByOwner(user.getId(), "INVALID_STATE", 0, 10));
//    }
}

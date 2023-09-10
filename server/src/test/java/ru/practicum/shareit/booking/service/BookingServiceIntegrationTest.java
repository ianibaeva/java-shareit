package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private UserDto createUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }

    private ItemDtoOut createItemDto(String name, String description, boolean available) {
        ItemDtoOut itemDto = new ItemDtoOut();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }

    private BookItemRequestDto createBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookItemRequestDto bookingDto = new BookItemRequestDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        return bookingDto;
    }

    @Test
    void addBookingTest() {
        UserDto addedUser1 = userService.addUser(createUserDto("name1", "email1@email.com"));
        UserDto addedUser2 = userService.addUser(createUserDto("name2", "email2@email.com"));
        itemService.addItem(createItemDto("item1 name", "item1 description", true), addedUser1.getId());
        itemService.addItem(createItemDto("item2 name", "item2 description", true), addedUser2.getId());

        LocalDateTime now = LocalDateTime.now();
        BookItemRequestDto bookingDto1 = createBookingDto(2L, now.plusSeconds(10L), now.plusSeconds(11L));
        BookingOutDto bookingDtoOut1 = bookingService.create(addedUser1.getId(), bookingDto1);

        assertEquals(1L, bookingDtoOut1.getId());
        assertEquals(Status.WAITING, bookingDtoOut1.getStatus());

        BookingOutDto updatedBookingDto1 = bookingService.update(addedUser2.getId(),
                bookingDtoOut1.getId(), true);

        assertEquals(Status.APPROVED, updatedBookingDto1.getStatus());

        List<BookingOutDto> bookingsDtoOut = bookingService.getAllByOwner(addedUser2.getId(),
                State.ALL.toString(), 0, 10);

        assertEquals(1, bookingsDtoOut.size());
    }


    @Test
    void update_whenBookingIdAndUserIdAreNotValid_ThrowsObjectNotFoundException() {
        Long userId = 3L;
        Long bookingId = 3L;

        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.update(userId, bookingId, true));
    }
}




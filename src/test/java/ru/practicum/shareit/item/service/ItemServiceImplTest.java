package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingItemDto;
import static ru.practicum.shareit.item.comment.mapper.CommentMapper.toCommentDto;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    private User user;
    private User user2;
    private UserDto userDto;
    private Item item;
    private ItemResponseDto itemDto;
    private ItemResponseDto itemDtoUpdate;
    private Comment comment;
    private Booking booking;
    private Booking lastBooking;
    private Booking pastBooking;
    private Booking nextBooking;
    private Booking futureBooking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("username");
        user.setEmail("email@email.com");

        user2 = new User();
        user2.setId(2L);
        user2.setName("username2");
        user2.setEmail("email2@email.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("username");
        userDto.setEmail("email@email.com");

        item = new Item();
        item.setId(1L);
        item.setName("item name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        itemDto = new ItemResponseDto();
        itemDto.setId(1L);
        itemDto.setName("item name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        itemDtoUpdate = new ItemResponseDto();
        itemDtoUpdate.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("comment");
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setItem(item);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);
        booking.setStart(LocalDateTime.now().minusDays(1L));
        booking.setEnd(LocalDateTime.now().plusDays(1L));

        lastBooking = new Booking();
        lastBooking.setId(2L);
        lastBooking.setItem(item);
        lastBooking.setBooker(user);
        lastBooking.setStatus(Status.APPROVED);
        lastBooking.setStart(LocalDateTime.now().minusDays(2L));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1L));

        pastBooking = new Booking();
        pastBooking.setId(3L);
        pastBooking.setItem(item);
        pastBooking.setBooker(user);
        pastBooking.setStatus(Status.APPROVED);
        pastBooking.setStart(LocalDateTime.now().minusDays(10L));
        pastBooking.setEnd(LocalDateTime.now().minusDays(9L));

        nextBooking = new Booking();
        nextBooking.setId(4L);
        nextBooking.setItem(item);
        nextBooking.setBooker(user);
        nextBooking.setStatus(Status.APPROVED);
        nextBooking.setStart(LocalDateTime.now().plusDays(1L));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2L));

        futureBooking = new Booking();
        futureBooking.setId(5L);
        futureBooking.setItem(item);
        futureBooking.setBooker(user);
        futureBooking.setStatus(Status.APPROVED);
        futureBooking.setStart(LocalDateTime.now().plusDays(10L));
        futureBooking.setEnd(LocalDateTime.now().plusDays(20L));
    }

    @Test
    void addNewItem_whenInvoked_returnItemDto() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item name");

        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponseDto actualItemDto = itemService.addItem(new ItemDtoOut(), userDto.getId());

        assertEquals(actualItemDto.getId(), 1L);
        assertEquals(actualItemDto.getName(), "item name");
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItem() {
        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("updated name");
        updatedItem.setDescription("updated description");
        updatedItem.setAvailable(false);
        updatedItem.setOwner(user);
        updatedItem.setRequestId(1L);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(updatedItem));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);

        ItemDtoOut updatedItemDtoOut = new ItemDtoOut();
        updatedItemDtoOut.setName("updated name");
        updatedItemDtoOut.setDescription("updated description");

        itemService.updateItem(user.getId(), updatedItemDtoOut, 1L);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item savedItem = itemArgumentCaptor.getValue();

        assertEquals("updated name", savedItem.getName());
        assertEquals("updated description", savedItem.getDescription());
    }

    @Test
    void updateItem_whenItemIsNotItemOwner_thenThrowObjectNotFoundException() {
        ItemDtoOut updatedItemDto = new ItemDtoOut();
        updatedItemDto.setName("updated name");
        updatedItemDto.setDescription("updated description");
        updatedItemDto.setAvailable(false);
        updatedItemDto.setRequestId(1L);

        ObjectNotFoundException itemNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(user.getId(), updatedItemDto, 1L));

        assertEquals(itemNotFoundException.getMessage(), String.format("Item with ID %s not found", 1L)
        );

    }

    @Test
    void getItemById() {
        Long userId = userDto.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        itemDto.setComments(new ArrayList<>());

        ItemResponseDto actualItemDto = itemService.getItemById(item.getId(), user.getId());

        assertEquals(itemDto, actualItemDto);
    }

    @Test
    void getItemById_whenItemIdIsInvalid_thenThrowNotFoundException() {
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        ObjectNotFoundException itemNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getItemById(user.getId(), itemDto.getId()));

        assertEquals(itemNotFoundException.getMessage(), String.format("User with ID: %s " +
                "does not have an item with ID: %s.", user.getId(), item.getId()));
    }

    @Test
    void getAllUserItemsTest() {
        itemDto.setComments(List.of(toCommentDto(comment)));
        itemDto.setLastBooking(toBookingItemDto(lastBooking));
        itemDto.setNextBooking(toBookingItemDto(nextBooking));
        List<ItemResponseDto> expectedItemsDto = List.of();

        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        List<Item> item = new ArrayList<>(List.of());
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong())).thenReturn(item);
        List<CommentResponseDto> comments = new ArrayList<>(List.of());
        when(commentRepository.findByItemIdIn(anyList(), any(Sort.class))).thenReturn(comments);
        when(bookingRepository.findAllByItemInAndStatus(anyList(), any(Status.class), any(Sort.class)))
                .thenReturn(List.of(lastBooking, nextBooking, pastBooking, futureBooking));

        List<ItemResponseDto> actualItemsDto = itemService.getAllUserItems(user.getId());

        assertEquals(actualItemsDto.size(), 0);
        assertEquals(actualItemsDto, expectedItemsDto);
    }

    @Test
    void getAllComments() {
        List<CommentResponseDto> expectedCommentsDto = List.of(toCommentDto(comment));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        List<CommentResponseDto> actualComments = itemService.getAllComments(item.getId());

        assertEquals(actualComments.size(), 1);
        assertEquals(actualComments, expectedCommentsDto);
    }

    @Test
    void searchItems() {
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        List<Item> items = List.of(item);
        when(itemRepository.searchAvailableItemsByNameOrDescription(anyString(), any(Sort.class))).thenReturn(items);

        List<ItemResponseDto> actualItemsDto = itemService.getAvailableItemBySearch("item", user.getId());

        assertEquals(1, actualItemsDto.size());
        assertEquals(1, actualItemsDto.get(0).getId());
        assertEquals("item name", actualItemsDto.get(0).getName());
    }

    @Test
    void createComment() {
        CommentResponseDto expectedCommentDto = toCommentDto(comment);
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDto actualCommentDto = itemService.createComment(user.getId(), new CommentRequestDto(), item.getId());

        assertEquals(expectedCommentDto, actualCommentDto);
    }

    @Test
    void createComment_whenItemIdIsNotValid_ThrowsObjectNotFoundException() {
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        ObjectNotFoundException itemNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> itemService.createComment(user.getId(), new CommentRequestDto(), item.getId()));

        assertEquals(itemNotFoundException.getMessage(), String.format("User with ID: %s " +
                "does not have an item with ID: %s.", user.getId(), item.getId()));
    }

    @Test
    void createComment_whenUserHasNotAnyBookings_ThrowsValidationException() {
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByUserBookings(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ValidationException userBookingsNotFoundException = assertThrows(ValidationException.class,
                () -> itemService.createComment(user.getId(), new CommentRequestDto(), item.getId()));

        assertEquals(userBookingsNotFoundException.getMessage(), String.format("User with ID %s must have at least one booking " +
                "for the item with ID %s.", user.getId(), item.getId()));

    }
}

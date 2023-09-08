package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.dto.CommentRequestDto;
import ru.practicum.shareit.item.comment.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

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

    private CommentRequestDto createCommentRequestDto(String text) {
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText(text);
        return commentRequestDto;
    }

    @Test
    void addItemTest() {
        UserDto addedUser = userService.addUser(createUserDto("user1", "user1@example.com"));
        ItemDtoOut itemDtoOut = createItemDto("item1 name", "item1 description", true);

        ItemResponseDto addedItem = itemService.addItem(itemDtoOut, addedUser.getId());

        assertNotNull(addedItem.getId());
        assertEquals(itemDtoOut.getName(), addedItem.getName());
        assertEquals(itemDtoOut.getDescription(), addedItem.getDescription());
    }

    @Test
    void updateItemTest() {
        UserDto addedUser = userService.addUser(createUserDto("user2", "user2@example.com"));
        ItemDtoOut itemDtoOut = createItemDto("item2 name", "item2 description", true);
        ItemResponseDto addedItem = itemService.addItem(itemDtoOut, addedUser.getId());

        ItemDtoOut updatedItemDto = createItemDto("updated name", "updated description", false);
        ItemResponseDto updatedItem = itemService.updateItem(addedItem.getId(), updatedItemDto, addedUser.getId());

        assertEquals(updatedItemDto.getName(), updatedItem.getName());
        assertEquals(updatedItemDto.getDescription(), updatedItem.getDescription());
    }

    @Test
    void getItemByIdTest() {
        UserDto addedUser = userService.addUser(createUserDto("user3", "user3@example.com"));
        ItemDtoOut itemDtoOut = createItemDto("item3 name", "item3 description", true);
        ItemResponseDto addedItem = itemService.addItem(itemDtoOut, addedUser.getId());

        ItemResponseDto retrievedItem = itemService.getItemById(addedItem.getId(), addedUser.getId());

        assertNotNull(retrievedItem);
        assertEquals(addedItem.getId(), retrievedItem.getId());
        assertEquals(itemDtoOut.getName(), retrievedItem.getName());
        assertEquals(itemDtoOut.getDescription(), retrievedItem.getDescription());
    }

    @Test
    void getAllUserItemsTest() {
        UserDto addedUser = userService.addUser(createUserDto("user4", "user4@example.com"));
        ItemDtoOut itemDtoOut1 = createItemDto("item4 name 1", "item4 description 1", true);
        ItemDtoOut itemDtoOut2 = createItemDto("item4 name 2", "item4 description 2", false);

        itemService.addItem(itemDtoOut1, addedUser.getId());
        itemService.addItem(itemDtoOut2, addedUser.getId());

        List<ItemResponseDto> userItems = itemService.getAllUserItems(addedUser.getId());

        assertNotNull(userItems);
        assertEquals(2, userItems.size());

        assertEquals(itemDtoOut1.getName(), userItems.get(0).getName());
        assertEquals(itemDtoOut2.getName(), userItems.get(1).getName());
    }

    @Test
    void getAvailableItemBySearchTest() {
        UserDto addedUser = userService.addUser(createUserDto("user5", "user5@example.com"));
        ItemDtoOut itemDtoOut1 = createItemDto("item5 name 1", "item5 description 1", true);
        ItemDtoOut itemDtoOut2 = createItemDto("item5 name 2", "item5 description 2", true);
        ItemDtoOut itemDtoOut3 = createItemDto("item5 name 3", "item5 description 3", false);

        itemService.addItem(itemDtoOut1, addedUser.getId());
        itemService.addItem(itemDtoOut2, addedUser.getId());
        itemService.addItem(itemDtoOut3, addedUser.getId());

        String searchText = "item5";

        List<ItemResponseDto> availableItems = itemService.getAvailableItemBySearch(searchText, addedUser.getId());

        assertNotNull(availableItems);
        assertEquals(2, availableItems.size());

        assertEquals(itemDtoOut1.getName(), availableItems.get(0).getName());
        assertEquals(itemDtoOut2.getName(), availableItems.get(1).getName());
    }

    @Test
    void createCommentWithoutBookingTest() {
        UserDto addedUser = userService.addUser(createUserDto("user6", "user6@example.com"));
        ItemDtoOut itemDtoOut = createItemDto("item6 name", "item6 description", true);
        ItemResponseDto addedItem = itemService.addItem(itemDtoOut, addedUser.getId());

        CommentRequestDto commentRequestDto = createCommentRequestDto("This is a test comment.");

        assertThrows(ValidationException.class, () -> {
            CommentResponseDto createdComment = itemService.createComment(addedUser.getId(), commentRequestDto, addedItem.getId());
        });
    }
}

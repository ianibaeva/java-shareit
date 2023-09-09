package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    private UserDto createUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userDto;
    }

    private ItemRequestDto createItemRequestDto(String description) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(description);
        return itemRequestDto;
    }

    @Test
    void addNewRequestTest() {
        UserDto addedUser = userService.addUser(createUserDto("user1", "user1@example.com"));
        ItemRequestDto itemRequestDto = createItemRequestDto("Test description");

        ItemRequestDto addedRequest = itemRequestService.addNewRequest(itemRequestDto, addedUser.getId());

        assertNotNull(addedRequest.getId());
        assertEquals("Test description", addedRequest.getDescription());
        assertNotNull(addedRequest.getCreated());
    }

    @Test
    void getRequestById_whenRequestIdIsNotValid_ThrowsObjectNotFoundException() {
        Long userId = 1L;
        Long requestId = 2L;

        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getRequestById(userId, requestId));
    }
}

package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    private final UserDto userDto;

    public UserServiceIntegrationTest() {
        userDto = new UserDto();
        userDto.setName("name");
        userDto.setEmail("email@email.com");
    }

    @Test
    void addUserTest() {
        UserDto actualUserDto = userService.addUser(userDto);

        assertEquals(1L, actualUserDto.getId());
        assertEquals("name", actualUserDto.getName());
        assertEquals("email@email.com", actualUserDto.getEmail());
    }

    @Test
    void getUserByInvalidId_thenThrowObjectNotFoundException() {
        Long userId = 2L;

        Assertions
                .assertThrows(ObjectNotFoundException.class, () -> userService.getUserById(userId));
    }
}

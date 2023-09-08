package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("johndoe@example.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John Doe");
        userDto.setEmail("johndoe@example.com");
    }

    @Test
    @Transactional
    void addUser_ValidUserDto_ShouldReturnUserDto() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.addUser(userDto);

        assertNotNull(result);
        assertEquals(userDto, result);
    }

    @Test
    @Transactional
    void updateUser_ValidUserDtoAndUserId_ShouldReturnUpdatedUserDto() {
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(userId);
        updatedUserDto.setName("Updated Name");
        updatedUserDto.setEmail("updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.updateUser(updatedUserDto, userId);

        assertNotNull(result);
        assertEquals(updatedUserDto, result);
    }

    @Test
    @Transactional
    void updateUser_WhenUserNotFound_ThrowsObjectNotFoundException() {
        Long userId = 1L;
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setName("Updated Name");
        updatedUserDto.setEmail("updated@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(updatedUserDto, userId));
    }

    @Test
    @Transactional(readOnly = true)
    void getUserById_WithExistingUserId_ShouldReturnUserDto() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userDto, result);
    }

    @Test
    @Transactional(readOnly = true)
    void getUserById_WhenUserNotFound_ThrowsObjectNotFoundException() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    @Transactional(readOnly = true)
    void getAllUsers_ReturnsListOfUserDto() {
        List<User> users = List.of(user);
        List<UserDto> expected = users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    @Transactional
    void deleteUser_ExistingUserId_DeletesUser() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        assertDoesNotThrow(() -> userService.deleteUser(userId));
    }

    @Test
    @Transactional
    void deleteUser_WhenUserNotFound_ThrowsObjectNotFoundException() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(userId));
    }
}


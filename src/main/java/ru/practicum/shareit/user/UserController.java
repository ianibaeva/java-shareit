package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@AllArgsConstructor
public class UserController {
    private InMemoryUserStorage inMemoryUserStorage;

    @GetMapping
    private List<UserDto> getAllUsers() {
        return inMemoryUserStorage
                .getAllUsers()
                .stream()
                .map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    private UserDto getUser(
            @PathVariable("id") int id) {
        try {
            return UserMapper
                    .toUserDto(inMemoryUserStorage.getById(id));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    private UserDto addUser(
            @RequestBody UserDto user) {
        return UserMapper
                .toUserDto(inMemoryUserStorage.addUser(UserMapper.toUser(user)));
    }

    @PatchMapping("/{id}")
    private UserDto updateUser(
            @RequestBody UserDto user,
            @PathVariable int id) {
        try {
            inMemoryUserStorage.updateUser(UserMapper.toUser(user), id);
            return UserMapper
                    .toUserDto(inMemoryUserStorage.getById(id));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    private void deleteUser(
            @PathVariable int id) {
        try {
            inMemoryUserStorage.deleteUser(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}

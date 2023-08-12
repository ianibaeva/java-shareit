package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = toUser(userDto);
        return toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Long userId) {
        User userFromStorage = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.debug("User with ID {} not found", userId);
                    throw new ObjectNotFoundException(String.format("User with ID: %s not found", userId));
                }
        );

        if (!Objects.isNull(userDto.getEmail())) {
            userFromStorage.setEmail(userDto.getEmail());
        }
        if (!Objects.isNull(userDto.getName())) {
            userFromStorage.setName(userDto.getName());
        }
        return toUserDto(userRepository.save(userFromStorage));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.debug("User with ID {} not found", userId);
                    throw new ObjectNotFoundException(String.format("User with ID: %s not found", userId));
                }
        );
        return toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream().map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
        } else {
            log.debug("User with ID {} not found", userId);
            throw new ObjectNotFoundException(String.format("User with ID: %s not found", userId));
        }
    }
}

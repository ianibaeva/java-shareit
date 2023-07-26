package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.Collection;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    public UserServiceImpl(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public Collection<User> getAllUsers() {
        return inMemoryUserStorage.getAllUsers();
    }

    @Override
    public User getById(int id) {
        try {
            User user = inMemoryUserStorage.getById(id);
            if (user == null) {
                throw new ValidationException(HttpStatus.NOT_FOUND, "User not found");
            }
            return user;
        } catch (NoSuchElementException e) {
            log.error("User not found with ID: {}", id);
            throw new ValidationException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @Override
    public User addUser(User user) {
        return inMemoryUserStorage.addUser(user);
    }

    @Override
    public void updateUser(User user, int id) {
        User existingUser = inMemoryUserStorage.getById(id);
        if (existingUser == null) {
            throw new ValidationException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (user.getId() == 0) {
            user.setId(existingUser.getId());
        }
        if (user.getName() == null) {
            user.setName(existingUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(existingUser.getEmail());
        }
        try {
            inMemoryUserStorage.updateUser(user, id);
            log.info("User {} has been updated", user.toString().toUpperCase());
        } catch (ValidationException e) {
            log.error("Error updating user with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Override
    public void deleteUser(int id) {
        inMemoryUserStorage.deleteUser(id);
    }
}

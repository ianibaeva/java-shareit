package ru.practicum.shareit.user.storage;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
@Component
public class InMemoryUserStorage {

    private Map<Integer, User> users = new HashMap<>();

    private int id = 1;

    public Collection<User> getAllUsers() {
        log.info("Users {}", users.toString().toUpperCase());
        return users.values();
    }

    public User getById(int id) {
        return users.get(id);
    }

    public User addUser(User user) {
        validate(user);
        user.setId(id++);
        users.put(user.getId(), user);
        log.info("User {} has been added", users.toString().toUpperCase());
        return user;
    }

    public void updateUser(User user, int id) {
        validate(user);
        users.put(id, user);
        log.info("User {} has been updated", users.toString().toUpperCase());
    }

    public void deleteUser(int id) throws ResponseStatusException {
        users.remove(id);
        log.info("User {} has been deleted", users.toString().toUpperCase());
    }

    private void validate(User user) {
        if (StringUtils.isBlank(user.getEmail()) || !(user.getEmail().contains("@"))) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Email must not be blank and should include @");
        }
        if (StringUtils.isBlank(user.getName()) || StringUtils.containsWhitespace(user.getName())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Username must not be blank or contain spaces");
        }
        for (User u : users.values()) {
            if (u.getId() == user.getId()) {
                continue;
            }
            if (u.getEmail().equals(user.getEmail())) {
                throw new ValidationException(HttpStatus.CONFLICT, "Email already exists");
            }
        }
    }
}
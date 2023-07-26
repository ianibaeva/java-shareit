package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> getAllUsers();

    User getById(int id);

    User addUser(User user);

    void updateUser(User user, int id);

    void deleteUser(int id);
}

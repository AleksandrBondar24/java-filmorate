package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    void save(User user);
    void update(User user);
    List<User> getUsers();
    User getUser(Long id);
}

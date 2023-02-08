package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public void saveFriend(Long id, Long friendId) {
        validatedId(id,friendId);
        storage.getUser(id).getFriends().add(friendId);
        storage.getUser(friendId).getFriends().add(id);
    }

    public void deleteFriend(Long id, Long friendId) {
        validatedId(id,friendId);
        storage.getUser(id).getFriends().remove(friendId);
        storage.getUser(friendId).getFriends().remove(id);
    }

    public List<User> getFriends(Long id) {
        return storage.getUser(id).getFriends().stream().map(storage::getUser).collect(Collectors.toList());
    }
    public List<User> getMutualFriends(Long id,Long otherId) {
        validatedId(id,otherId);
        Set<Long> friends = storage.getUser(id).getFriends();
        return storage.getUser(otherId).getFriends().stream().filter(friends::contains).
                map(storage::getUser).collect(Collectors.toList());
    }
    public User getUser(Long id) {
        return storage.getUser(id);
    }
    public void save(User user) {
        storage.save(user);
    }
    public void update(User user) {
        storage.update(user);
    }
    public List<User> getUsers() {
        return storage.getUsers();
    }
    public void validatedId(Long id, Long friendId) {
        if (id <= 0 || friendId <=0) {
            throw new NotFoundException("Идентификатор должен быть положительным числом.");
        }
    }
}

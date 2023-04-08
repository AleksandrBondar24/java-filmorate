package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.enums.EventType;
import ru.yandex.practicum.filmorate.util.enums.Operation;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractService<User> {
    private final UserStorage userStorage;
    private final FeedStorage feedStorage;

    @Override
    public void add(Long userId, Long friendId) {
        super.add(userId, friendId);
        userStorage.addFriend(userId, friendId);
        feedStorage.saveFeed(userId, EventType.FRIEND, Operation.ADD, friendId);
    }

    @Override
    public void delete(Long userId, Long friendId) {
        super.delete(userId, friendId);
        userStorage.deleteFriend(userId, friendId);
        feedStorage.saveFeed(userId, EventType.FRIEND, Operation.REMOVE, friendId);
    }

    public Set<User> getListFriends(Long userId) {
        super.validateId(userId);
        return userStorage.getListFriends(userId);
    }

    public Set<User> getMutualFriends(Long userId, Long otherId) {
        super.validateIds(userId, otherId);
        Set<User> friends = userStorage.getListFriends(userId);
        return userStorage.getListFriends(otherId).stream().
                filter(friends::contains).
                collect(Collectors.toSet());
    }

    public User getUser(Long userId) {
        super.validateId(userId);
        return userStorage.getUser(userId);
    }

    @Override
    public User save(User user, BindingResult result) {
        return userStorage.save(super.save(user, result));
    }

    @Override
    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> getListAllUsers() {
        return userStorage.getUsers();
    }

    @Override
    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    public List<Feed> getFeed(Long id) {
        User user = userStorage.getUser(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id: " + id + " не найден.");
        }
        return feedStorage.getFeed(id);
    }
}

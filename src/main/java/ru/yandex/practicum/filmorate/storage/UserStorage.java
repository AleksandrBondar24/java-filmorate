package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {
    long save(User user);

    User update(User user);

    List<User> getUsers();

    User getUser(Long id);

    Set<User> getListFriends(Long idUser);

    void addFriend(Long idUser, Long friendId);

    void deleteFriend(Long idUser, Long friendId);
}

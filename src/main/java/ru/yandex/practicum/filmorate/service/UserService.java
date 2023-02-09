package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService extends AbstractService<User> {
    private final UserStorage storage;

    @Override
    public void add(Long idUser, Long friendId) {
        super.add(idUser, friendId);
        storage.getUser(idUser).getFriends().add(friendId);
        storage.getUser(friendId).getFriends().add(idUser);
    }

    @Override
    public void delete(Long idUser, Long friendId) {
        super.delete(idUser, friendId);
        storage.getUser(idUser).getFriends().remove(friendId);
        storage.getUser(friendId).getFriends().remove(idUser);
    }


    public List<User> getListFriends(Long id) {
        super.validateId(id);
        return storage.getUser(id).getFriends().stream().
                map(storage::getUser).
                collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long idUser, Long otherId) {
        super.validateIds(idUser, otherId);
        Set<Long> friends = storage.getUser(idUser).getFriends();
        return storage.getUser(otherId).getFriends().stream().
                filter(friends::contains).
                map(storage::getUser).
                collect(Collectors.toList());
    }

    public User getUser(Long id) {
        super.validateId(id);
        return storage.getUser(id);
    }

    @Override
    public User save(User user, BindingResult result) {
        return storage.save(super.save(user, result));
    }

    @Override
    public User update(User user) {
        return storage.update(super.update(user));
    }


    public List<User> getListAllUsers() {
        return storage.getUsers();
    }

    @Override
    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}

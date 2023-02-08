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
    public void add(Long id, Long friendId) {
        super.add(id, friendId);
        storage.getUser(id).getFriends().add(friendId);
        storage.getUser(friendId).getFriends().add(id);
    }

    @Override
    public void delete(Long id, Long friendId) {
        super.delete(id, friendId);
        storage.getUser(id).getFriends().remove(friendId);
        storage.getUser(friendId).getFriends().remove(id);
    }

    @Override
    public List<User> getList(Long id) {
        super.validateId(id);
        return storage.getUser(id).getFriends().stream().map(storage::getUser).collect(Collectors.toList());
    }

    public List<User> getMutualFriends(Long id, Long otherId) {
        super.validateIds(id,otherId);
        Set<Long> friends = storage.getUser(id).getFriends();
        return storage.getUser(otherId).getFriends().stream().filter(friends::contains).
                map(storage::getUser).collect(Collectors.toList());
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

    @Override
    public List<User> getListModels() {
        return storage.getUsers();
    }

    @Override
    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}

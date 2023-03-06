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
    private final UserStorage storageUser;

    @Override
    public void add(Long userId, Long friendId) {
        super.add(userId, friendId);
        storageUser.addFriend(userId, friendId);
    }

    @Override
    public void delete(Long userId, Long friendId) {
        super.delete(userId, friendId);
        storageUser.deleteFriend(userId, friendId);
    }

    public Set<User> getListFriends(Long userId) {
        super.validateId(userId);
        return storageUser.getListFriends(userId);
    }

    public Set<User> getMutualFriends(Long userId, Long otherId) {
        super.validateIds(userId, otherId);
        Set<User> friends = storageUser.getListFriends(userId);
        return storageUser.getListFriends(otherId).stream().
                filter(friends::contains).
                collect(Collectors.toSet());
    }

    public User getUser(Long userId) {
        super.validateId(userId);
        return storageUser.getUser(userId);
    }

    @Override
    public User save(User user, BindingResult result) {
        long id = storageUser.save(super.save(user, result));
        return storageUser.getUser(id);
    }

    @Override
    public User update(User user) {
        return storageUser.update(user);
    }

    public List<User> getListAllUsers() {
        return storageUser.getUsers();
    }

    @Override
    public void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}

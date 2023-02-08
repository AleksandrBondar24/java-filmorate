package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        users.put(user.getId(),user);
    }

    @Override
    public void update(User user) throws NotFoundException{
        if (users.get(user.getId()) == null) {
            throw new NotFoundException("Пользователь с идентификатором: " + user.getId() + " не найден.");
        }
        users.put(user.getId(), user);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
    @Override
    public User getUser(Long id) throws NotFoundException{
        if (users.get(id) == null) {
            throw new NotFoundException("Пользователь с идентификатором: " + id + " не найден.");
        }
        return users.get(id);
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController extends Controller<User> {
    private final UserService service;

    @PostMapping
    @Override
    public User create(@Valid @RequestBody User user, BindingResult result) {
        service.save(super.create(user, result));
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    @Override
    public User update(@Valid @RequestBody User user) {
        service.update(super.update(user));
        log.debug("Обновлен пользователь: {}", user);
        return user;
    }

    @GetMapping
    @Override
    public List<User> getListModels() {
        log.debug("Получен список пользователей: {} ", service.getUsers());
        return service.getUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    @Override
    protected void add(@PathVariable Long id, @PathVariable Long friendId) {
        service.saveFriend(id, friendId);
        log.debug("Пользователь с идетификатором: " + id + " добавил друга: " + friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @Override
    protected void delete(@PathVariable Long id, @PathVariable Long friendId) {
        service.deleteFriend(id, friendId);
        log.debug("Пользователь с идетификатором: " + id + " удалил друга: " + friendId);
    }

    @GetMapping("/{id}/friends")
    @Override
    protected List<User> getList(@PathVariable Long id) {
        log.debug("Получен список друзей пользователя с идентификатором: " + id);
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    protected List<User> getList(@PathVariable Long id, @PathVariable Long otherId) {
        log.debug("Получен список общих друзей пользователей с идентификаторами: " + id + " и " + otherId);
        return service.getMutualFriends(id, otherId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        User user = service.getUser(id);
        log.debug("Получен пользователь с идентификатором: " + id);
        return user;
    }

    @Override
    protected void validate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}


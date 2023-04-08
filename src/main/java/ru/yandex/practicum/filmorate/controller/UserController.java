package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @PostMapping
    public User create(@Valid @RequestBody User user, BindingResult result) {
        User user1 = userService.save(user, result);
        log.debug("Добавлен пользователь: {}", user1);
        return user1;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        User user1 = userService.update(user);
        log.debug("Обновлен пользователь: {}", user1);
        return user1;
    }

    @GetMapping
    public List<User> getListModels() {
        log.debug("Получен список пользователей: {} ", userService.getListAllUsers());
        return userService.getListAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    protected void add(@PathVariable Long id, @PathVariable Long friendId) {
        userService.add(id, friendId);
        log.debug("Пользователь с идетификатором: " + id + " добавил друга: " + friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    protected void delete(@PathVariable Long id, @PathVariable Long friendId) {
        userService.delete(id, friendId);
        log.debug("Пользователь с идетификатором: " + id + " удалил друга: " + friendId);
    }

    @GetMapping("/{id}/friends")
    protected Set<User> getList(@PathVariable Long id) {
        log.debug("Получен список друзей пользователя с идентификатором: " + id);
        return userService.getListFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    protected Set<User> getListOfMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.debug("Получен список общих друзей пользователей с идентификаторами: " + id + " и " + otherId);
        return userService.getMutualFriends(id, otherId);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        log.debug("Получен пользователь с идентификатором: " + id);
        return user;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        log.info("Пользователь с идентификатором: " + id + " удален.");
    }
    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendation(@PathVariable Long id) {
        return filmService.getRecommendation(id);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getFeed(@PathVariable Long id) {
        List<Feed> feed = userService.getFeed(id);
        log.info("Получена лента событий пользователя с идентификатором: {} ", id);
        return feed;
    }
}


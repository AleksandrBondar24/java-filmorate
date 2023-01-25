package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    private int generatorIdUser = 1;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try {
            if (user.getName() == null) {
                user.setName(user.getLogin());
            }
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            user.setId(generatorIdUser);
            users.put(user.getId(), user);
            log.debug("Добавлен пользователь: " + user.getName());
            generatorIdUser++;
            return ResponseEntity.ok().body(user);
        } catch (ValidationException e) {
            log.debug(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(user);
        }
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        try {
            if (user.getId() == 0 || !users.containsKey(user.getId())) {
                throw new ValidationException("Ошибка валидации данных.");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(users.get(user.getId()).getName());
            }
            users.put(user.getId(), user);
            log.debug("Обновлен пользователь: " + user.getName());
            return ResponseEntity.ok().body(user);
        } catch (ValidationException e) {
            log.debug(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(user);
        }
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<ArrayList<User>> getUsers() {
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }
}


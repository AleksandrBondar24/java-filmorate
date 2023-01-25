package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private Long generatorIdUser = 1L;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try {
            nameValidationCreate(user);
            user.setId(generatorIdUser);
            users.put(user.getId(), user);
            log.debug("Добавлен пользователь: " + user);
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
            idValidation(user);
            nameValidationUpdate(user);
            users.put(user.getId(), user);
            log.debug("Обновлен пользователь: " + user);
            return ResponseEntity.ok().body(user);
        } catch (ValidationException e) {
            log.debug(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(user);
        }
    }

    @GetMapping
    public ResponseEntity<ArrayList<User>> getUsers() {
        return ResponseEntity.ok(new ArrayList<>(users.values()));
    }

    private void idValidation(User user) {
        if (user.getId() == 0 || !users.containsKey(user.getId())) {
            throw new ValidationException("Ошибка валидации данных.");
        }
    }

    private void nameValidationUpdate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(users.get(user.getId()).getName());
        }
    }

    private void nameValidationCreate(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}


package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.util.NotFoundException;
import ru.yandex.practicum.filmorate.util.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@Slf4j
@RequestMapping("/users")
public class UserController{
    private final Map<Long, User> users = new HashMap<>();
    private Long generatorId = 1L;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try {
            nameValidation(user);
            user.setId(generatorId);
            users.put(user.getId(), user);
            log.debug("Добавлен пользователь: " + user);
            generatorId++;
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
            nameValidation(user);
            users.put(user.getId(), user);
            log.debug("Обновлен пользователь: " + user);
            return ResponseEntity.ok().body(user);
        } catch (NotFoundException e) {
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
            throw new NotFoundException();
        }
    }

    private void nameValidation(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}


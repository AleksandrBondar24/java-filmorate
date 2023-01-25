package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    public static final LocalDate data = LocalDate.of(1895, 12, 28);
    private int generatorIdFilm = 1;

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        try {
            if (film.getReleaseDate().isBefore(data) || film.getReleaseDate().isEqual(data)) {
                throw new ValidationException("Ошибка валидации данных.");
            }
            film.setId(generatorIdFilm);
            films.put(film.getId(), film);
            log.debug("Добавлен фильм: " + film.getName());
            generatorIdFilm++;
            return ResponseEntity.ok(film);
        } catch (ValidationException e) {
            log.debug(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(film);
        }
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        try {
            if (film.getId() == 0 ||
                    !films.containsKey(film.getId()) ||
                    film.getReleaseDate().isBefore(data) ||
                    film.getReleaseDate().isEqual(data)) {
                throw new ValidationException("Ошибка валидации данных.");
            }

            films.put(film.getId(), film);
            log.debug("Обновлен фильм: " + film.getName());
            return ResponseEntity.ok(film);
        } catch (ValidationException e) {
            log.debug(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film);
        }
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<ArrayList<Film>> getFilms() {
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }
}


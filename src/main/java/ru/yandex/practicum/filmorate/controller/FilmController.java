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
    private final Map<Long, Film> films = new HashMap<>();
    public static final LocalDate data = LocalDate.of(1895, 12, 28);
    private Long generatorIdFilm = 1L;

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        try {
            releaseDateValidation(film);
            film.setId(generatorIdFilm);
            films.put(film.getId(), film);
            log.debug("Добавлен фильм: " + film);
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
            idValidation(film);
            releaseDateValidation(film);
            films.put(film.getId(), film);
            log.debug("Обновлен фильм: " + film);
            return ResponseEntity.ok(film);
        } catch (ValidationException e) {
            log.debug(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film);
        }
    }

    @GetMapping
    public ResponseEntity<ArrayList<Film>> getFilms() {
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }

    private void idValidation(Film film) {
        if (film.getId() == 0 || !films.containsKey(film.getId())) {
            throw new ValidationException("Ошибка валидации данных.");
        }
    }

    private void releaseDateValidation(Film film) {
        if (film.getReleaseDate().isBefore(data) || film.getReleaseDate().isEqual(data)) {
            throw new ValidationException("Ошибка валидации данных.");
        }
    }
}


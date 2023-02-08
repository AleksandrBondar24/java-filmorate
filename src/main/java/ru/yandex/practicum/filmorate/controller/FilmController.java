package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequestMapping("/films")
@RestController
@RequiredArgsConstructor
public class FilmController extends Controller<Film> {
    public static final LocalDate data = LocalDate.of(1895, 12, 28);
    private final FilmService service;

    @PostMapping
    @Override
    public Film create(@Valid @RequestBody Film film, BindingResult result) {
        service.save(super.create(film, result));
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    @Override
    public Film update(@Valid @RequestBody Film film) {
        service.update(super.update(film));
        log.debug("Обновлен фильм: {}", film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    @Override
    protected void add(@PathVariable Long id, @PathVariable Long userId) {
        service.saveLike(id, userId);
        log.debug("Пользователь с идентификатором: " + userId + " поставил лайк фильму: " + id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @Override
    protected void delete(@PathVariable Long id, @PathVariable Long userId) {
        service.deleteLike(id, userId);
        log.debug("Пользователь с идентификатором: " + userId + " удалил свой лайк фильму: " + id);
    }

    @GetMapping("/popular")
    @Override
    protected List<Film> getList(@RequestParam(value = "count", defaultValue = "10", required = false) Long count) {
        log.debug("Получен список из " + count + " лучших фильмов.");
        return service.getFilmsBest(count);
    }

    @GetMapping
    @Override
    public List<Film> getListModels() {
        log.debug("Получен список фильмов: {} ", service.getFilms());
        return service.getFilms();
    }


    @Override
    protected void validate(Film film) {
        if (film.getReleaseDate().isBefore(data)) {
            throw new ValidationException("Дата релиза фильма не должна быть раньше 1985.12.28");
        }
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        Film film = service.getFilm(id);
        log.debug("Получен фильм с идентификатором: " + id);
        return film;
    }
}





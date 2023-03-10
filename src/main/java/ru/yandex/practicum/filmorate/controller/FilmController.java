package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FilmController {
    private final FilmService service;

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film, BindingResult result) {
        Film film1 = service.save(film, result);
        log.debug("Добавлен фильм: {}", film1);
        return film1;
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        Film film1 = service.update(film);
        log.debug("Обновлен фильм: {}", film1);
        return film1;
    }

    @PutMapping("/films/{id}/like/{userId}")
    protected void add(@PathVariable Long id, @PathVariable Long userId) {
        service.add(id, userId);
        log.debug("Пользователь с идентификатором: " + userId + " поставил лайк фильму: " + id);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    protected void delete(@PathVariable Long id, @PathVariable Long userId) {
        service.delete(id, userId);
        log.debug("Пользователь с идентификатором: " + userId + " удалил свой лайк фильму: " + id);
    }

    @GetMapping("/films/popular")
    protected List<Film> getList(@RequestParam(value = "count", defaultValue = "10", required = false) Long count) {
        log.debug("Получен список из " + count + " лучших фильмов.");
        return service.getListFilmBest(count);
    }

    @GetMapping("/films")
    public List<Film> getListModels() {
        List<Film> films = service.getListAllFilms();
        log.debug("Получен список фильмов: {} ", films);
        return films;
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Long id) {
        Film film = service.getFilm(id);
        log.debug("Получен фильм с идентификатором: " + id);
        return film;
    }

    @GetMapping("/genres")
    public List<FilmGenre> getFilmGenres() {
        List<FilmGenre> filmGenres = service.getFilmGenres();
        log.debug("Получен список жанров: {} ", filmGenres);
        return filmGenres;
    }

    @GetMapping("/genres/{id}")
    public FilmGenre getFilmGenre(@PathVariable Integer id) {
        FilmGenre filmGenre = service.getFilmGenre(id);
        log.debug("Получен жанр с идентификатором: {} ", id);
        return filmGenre;
    }

    @GetMapping("/mpa")
    public List<Mpa> getMpaRatings() {
        List<Mpa> mpaRatings = service.getMpaRatings();
        log.debug("Получен список MPA рейтингов: {} ", mpaRatings);
        return mpaRatings;
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaRating(@PathVariable Integer id) {
        Mpa mpaRating = service.getMpaRating(id);
        log.debug("Получен MPA рейтинг с идентификатором: {} ", id);
        return mpaRating;
    }
}





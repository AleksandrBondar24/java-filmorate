package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class FilmGenreController {
    private final FilmService service;

    @GetMapping
    public List<FilmGenre> getFilmGenres() {
        List<FilmGenre> filmGenres = service.getFilmGenres();
        log.debug("Получен список жанров: {} ", filmGenres);
        return filmGenres;
    }

    @GetMapping("/{id}")
    public FilmGenre getFilmGenre(@PathVariable Integer id) {
        FilmGenre filmGenre = service.getFilmGenre(id);
        log.debug("Получен жанр с идентификатором: {} ", id);
        return filmGenre;
    }
}

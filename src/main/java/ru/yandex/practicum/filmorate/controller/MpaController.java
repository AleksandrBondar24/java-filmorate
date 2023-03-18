package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService service;

    @GetMapping
    public List<Mpa> getMpaRatings() {
        List<Mpa> mpaRatings = service.getMpaRatings();
        log.debug("Получен список MPA рейтингов: {} ", mpaRatings);
        return mpaRatings;
    }

    @GetMapping("/{id}")
    public Mpa getMpaRating(@PathVariable Integer id) {
        Mpa mpaRating = service.getMpaRating(id);
        log.debug("Получен MPA рейтинг с идентификатором: {} ", id);
        return mpaRating;
    }
}

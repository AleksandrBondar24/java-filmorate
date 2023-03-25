package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    List<Film> getFilms();

    Film getFilm(Long id);

    void addLikes(Long idFilm, Long userId);

    void deleteLikes(Long idFilm, Long userId);
    List<Film> getListFilmBest(Long count);
}

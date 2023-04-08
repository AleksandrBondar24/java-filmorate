package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Film save(Film film);

    Film update(Film film);

    List<Film> getFilms();

    Film getFilm(Long id);

    void addLikes(Long idFilm, Long userId);

    void deleteLikes(Long idFilm, Long userId);
    List<Film> getListFilmBest(Long count);

    void deleteFilm(Long filmId);
    List<Film> searchFilms(String query, String by);

    List<Film> getFilmByDirectorByYear(Director director, String sortBy);

    List<Film> getFilmByDirectorByLikes(Director director, String sortBy);

    List<Film> assignDirectors(ResultSet rs, List<Film> films, Map<Long, Set<Director>> filmsDirectors) throws SQLException;

    List<Film> getRecommendation(Long id);
}

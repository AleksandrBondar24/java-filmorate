package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.util.enums.EventType;
import ru.yandex.practicum.filmorate.util.enums.Operation;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService extends AbstractService<Film> {
    public static final LocalDate data = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final FilmGenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;
    private final FeedStorage feedStorage;

    @Override
    public void add(Long filmId, Long userId) {
        super.add(filmId, userId);
        filmStorage.addLikes(filmId, userId);
        feedStorage.saveFeed(userId, EventType.LIKE, Operation.ADD, filmId);
    }

    @Override
    public void delete(Long filmId, Long userId) {
        super.delete(filmId, userId);
        filmStorage.deleteLikes(filmId, userId);
        feedStorage.saveFeed(userId, EventType.LIKE, Operation.REMOVE, filmId);
    }

    public List<Film> getListFilmBest(Long count) {
        super.validateId(count);
        return filmStorage.getListFilmBest(count);
    }

    @Override
    public Film save(Film film, BindingResult result) {
        return filmStorage.save(super.save(film, result));
    }

    @Override
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> getListAllFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(Long filmID) {
        super.validateId(filmID);
        return filmStorage.getFilm(filmID);
    }

    @Override
    public void validate(Film film) {
        if (film.getReleaseDate().isBefore(data)) {
            throw new ValidationException("Дата релиза фильма не должна быть раньше 1895.12.28");
        }
    }

    public List<FilmGenre> getFilmGenres() {
        return genreStorage.getFilmGenres();
    }

    public FilmGenre getFilmGenre(Long genreID) {
        return genreStorage.getFilmGenre(genreID);

    }

    public List<Mpa> getMpaRatings() {
        return mpaStorage.getMpaRatings();
    }

    public Mpa getMpaRating(Long mpaId) {
        return mpaStorage.getMpaRating(mpaId);
    }

    public void deleteFilm(Long filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by);
    }

    public List<Film> getFilmByDirector(Long directorId, String sortBy) {
        Director director = directorStorage.getDirectorById(directorId);
        if (sortBy.equals("year")) {
            return filmStorage.getFilmByDirectorByYear(director, sortBy);
        }
        return filmStorage.getFilmByDirectorByLikes(director, sortBy);
    }

    public List<Film> getRecommendation(Long id) {
        return filmStorage.getRecommendation(id);
    }
}

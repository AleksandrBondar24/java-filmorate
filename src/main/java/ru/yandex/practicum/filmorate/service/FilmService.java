package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService extends AbstractService<Film> {
    public static final LocalDate data = LocalDate.of(1895, 12, 28);
    private final FilmStorage storageFilm;
    private final FilmGenreStorage storageGenre;
    private final MpaStorage storageMpa;

    @Override
    public void add(Long filmId, Long userId) {
        super.add(filmId, userId);
        storageFilm.addLikes(filmId, userId);
    }

    @Override
    public void delete(Long filmId, Long userId) {
        super.delete(filmId, userId);
        storageFilm.deleteLikes(filmId, userId);
    }

    public List<Film> getListFilmBest(Long count) {
        super.validateId(count);
        return storageFilm.getListFilmBest(count);
    }

    @Override
    public Film save(Film film, BindingResult result) {
        return storageFilm.save(super.save(film, result));
    }

    @Override
    public Film update(Film film) {
        return storageFilm.update(film);
    }

    public List<Film> getListAllFilms() {
        return storageFilm.getFilms();
    }

    public Film getFilm(Long filmID) {
        super.validateId(filmID);
        return storageFilm.getFilm(filmID);
    }

    @Override
    public void validate(Film film) {
        if (film.getReleaseDate().isBefore(data)) {
            throw new ValidationException("Дата релиза фильма не должна быть раньше 1895.12.28");
        }
    }

    public List<FilmGenre> getFilmGenres() {
        return storageGenre.getFilmGenres();
    }

    public FilmGenre getFilmGenre(Integer genreID) {
        return storageGenre.getFilmGenre(genreID);

    }

    public List<Mpa> getMpaRatings() {
        return storageMpa.getMpaRatings();
    }

    public Mpa getMpaRating(Integer mpaId) {
        return storageMpa.getMpaRating(mpaId);
    }
}

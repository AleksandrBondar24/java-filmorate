package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService extends AbstractService<Film> {
    public static final LocalDate data = LocalDate.of(1895, 12, 28);
    private final FilmStorage storageFilm;

    @Override
    public void add(Long idFilm, Long userId) {
        super.add(idFilm, userId);
        storageFilm.getFilm(idFilm).getLikes().add(userId);
        storageFilm.getFilm(idFilm).setRating(storageFilm.getFilm(idFilm).getLikes().size());
    }

    @Override
    public void delete(Long idFilm, Long userId) {
        super.delete(idFilm, userId);
        storageFilm.getFilm(idFilm).getLikes().remove(userId);
        storageFilm.getFilm(idFilm).setRating(storageFilm.getFilm(idFilm).getLikes().size());
    }

    public List<Film> getListFilmBest(Long count) {
        super.validateId(count);
        return storageFilm.getFilms().stream().
                sorted(Comparator.comparingInt(Film::getRating).reversed()).
                limit(count).
                collect(Collectors.toList());
    }

    @Override
    public Film save(Film film, BindingResult result) {
        return storageFilm.save(super.save(film, result));
    }

    @Override
    public Film update(Film film) {
        return storageFilm.update(super.update(film));
    }

    public List<Film> getListAllFilms() {
        return storageFilm.getFilms();
    }

    public Film getFilm(Long id) {
        super.validateId(id);
        return storageFilm.getFilm(id);
    }

    @Override
    public void validate(Film film) {
        if (film.getReleaseDate().isBefore(data)) {
            throw new ValidationException("Дата релиза фильма не должна быть раньше 1985.12.28");
        }
    }
}

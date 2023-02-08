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
    private final FilmStorage storage;
    private final UserStorage storageUser;

    @Override
    public void add(Long id, Long userId) {
        super.add(id, userId);
        storage.getFilm(id).getLikes().add(storageUser.getUser(userId).getId());
    }

    @Override
    public void delete(Long id, Long userId) {
        super.delete(id, userId);
        storage.getFilm(id).getLikes().remove(storageUser.getUser(userId).getId());
    }

    public List<Film> getList(Long count) {
        super.validateId(count);
        List<Film> list = storage.getFilms().stream().sorted(Comparator.comparingInt(film -> film.getLikes().size())).
                collect(Collectors.toList());
        Collections.reverse(list);
        return list.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public Film save(Film film, BindingResult result) {
        return storage.save(super.save(film, result));
    }

    @Override
    public Film update(Film film) {
        return storage.update(super.update(film));
    }

    public List<Film> getListModels() {
        return storage.getFilms();
    }

    public Film getFilm(Long id) {
        super.validateId(id);
        return storage.getFilm(id);
    }

    @Override
    public void validate(Film film) {
        if (film.getReleaseDate().isBefore(data)) {
            throw new ValidationException("Дата релиза фильма не должна быть раньше 1985.12.28");
        }
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage storage;
    private final UserStorage storageUser;


    public void saveLike(Long id, Long userId) {
        storage.getFilm(id).getLikes().add(storageUser.getUser(userId).getId());
    }

    public void deleteLike(Long id, Long userId) {
        storage.getFilm(id).getLikes().remove(storageUser.getUser(userId).getId());
    }

    public List<Film> getFilmsBest(Long count) {
        List<Film> list = storage.getFilms().stream().sorted(Comparator.comparingInt(film -> film.getLikes().size())).
                collect(Collectors.toList());
        Collections.reverse(list);
        return list.stream().limit(count).collect(Collectors.toList());
    }

    public void save(Film film) {
        storage.save(film);
    }
    public void update(Film film) {
        storage.update(film);
    }

    public List<Film> getFilms() {
        return storage.getFilms();
    }

    public Film getFilm(Long id) {
        return storage.getFilm(id);
    }
}

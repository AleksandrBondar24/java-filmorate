package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public void save(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void update(Film film) throws NotFoundException {
        if (films.get(film.getId()) == null) {
            throw new NotFoundException("Фильм с идентификатором: " + film.getId() + " не найден.");
        }
        films.put(film.getId(), film);
    }
    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
    @Override
    public Film getFilm(Long id) throws NotFoundException{
        if (films.get(id) == null) {
            throw new NotFoundException("Фильм с идентификатором: " + id + " не найден.");
        }
        return films.get(id);
    }
}

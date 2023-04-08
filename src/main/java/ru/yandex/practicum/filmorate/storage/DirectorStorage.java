package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director create(Director director);

    Director getDirectorById(Long directorId);

    Director update(Director director);

    void delete(Long directorId);

    List<Director> getDirectors();
}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface FilmGenreStorage {
    List<FilmGenre> getFilmGenres();

    FilmGenre getFilmGenre(Long genreId);
}

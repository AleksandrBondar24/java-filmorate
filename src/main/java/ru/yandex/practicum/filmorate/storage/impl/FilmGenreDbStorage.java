package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Component
@RequiredArgsConstructor
@Primary
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public List<FilmGenre> getFilmGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRE", ((rs, rowNum) -> genreMapper(rs)));
    }


    @Override
    public FilmGenre getFilmGenre(Integer genreId) {
        return jdbcTemplate.query("SELECT * FROM GENRE WHERE genre_id=?",
                        ((rs, rowNum) -> genreMapper(rs)), genreId).
                stream().
                findAny().
                orElseThrow(() -> new NotFoundException("Жанр с id: " + genreId + "не был найден."));
    }
    private FilmGenre genreMapper(ResultSet rs) throws SQLException {
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setId(rs.getInt("genre_id"));
        filmGenre.setName(rs.getString("name_genre"));
        return filmGenre;
    }
}

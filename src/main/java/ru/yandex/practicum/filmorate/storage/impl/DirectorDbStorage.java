package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("director_id");
        Long directorId = simpleJdbcInsert.executeAndReturnKey(director.toMap()).longValue();
        director.setId(directorId);
        return director;
    }

    @Override
    public Director getDirectorById(Long directorId) {
        return jdbcTemplate.query("SELECT * FROM DIRECTORS WHERE director_id = ?",
                        ((rs, rowNum) -> directorMapper(rs)), directorId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Режиссер с id: " + directorId + " не был найден."));
    }

    @Override
    public Director update(Director director) {
        int row = jdbcTemplate
                .update("UPDATE DIRECTORS SET name_director = ? WHERE director_id = ?", director.getName(), director.getId());
        if (row == 0) {
            throw new NotFoundException("Режиссер с id: " + director.getId() + "не был найден.");
        }
        return getDirectorById(director.getId());
    }

    @Override
    public void delete(Long directorId) {
        jdbcTemplate.update("DELETE FROM DIRECTORS WHERE director_id = ?", directorId);
    }

    @Override
    public List<Director> getDirectors() {
        return jdbcTemplate.query("SELECT * FROM DIRECTORS", ((rs, rowNum) -> directorMapper(rs)));
    }

    private Director directorMapper(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name_director"));
        return director;
    }
}


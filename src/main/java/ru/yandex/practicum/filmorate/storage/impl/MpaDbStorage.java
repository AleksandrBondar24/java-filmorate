package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Component
@RequiredArgsConstructor
@Primary
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public Mpa getMpaRating(Integer mpaId) {
        return jdbcTemplate.query("SELECT * FROM MPA WHERE mpa_id=?",
                        ((rs, rowNum) -> mpaMapper(rs)), mpaId).
                stream().
                findAny().
                orElseThrow(() -> new NotFoundException("Рейтинг mpa с id: " + mpaId + "не был найден."));
    }
    @Override
    public List<Mpa> getMpaRatings() {
        return jdbcTemplate.query("SELECT * FROM MPA", ((rs, rowNum) -> mpaMapper(rs)));
    }
    private Mpa mpaMapper(ResultSet rs) throws SQLException {
        Mpa mpaRating = new Mpa();
        mpaRating.setId(rs.getInt("mpa_id"));
        mpaRating.setName(rs.getString("name_mpa"));
        return mpaRating;
    }
}

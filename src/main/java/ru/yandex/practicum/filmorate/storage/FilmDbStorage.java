package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film save(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILM")
                .usingGeneratedKeyColumns("film_id");
        long id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();
        film.setId(id);
        saveGenre(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        int row = jdbcTemplate.
                update("UPDATE FILM SET name=?, description=?, release_date=?, duration=?, rate=?, mpa_id=? WHERE film_id=?",
                        film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getRate(), film.getMpa().getId(),
                        film.getId());
        if (row == 0) {
            throw new NotFoundException("Фильм с id: " + film.getId() + "не был найден.");
        }
        saveGenre(film);
        return getFilm(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        return jdbcTemplate.query("SELECT * FROM FILM", ((rs, rowNum) -> filmMapper(rs)));
    }

    @Override
    public Film getFilm(Long idFilm) {
        return jdbcTemplate.query("SELECT * FROM FILM WHERE film_id=?",
                        ((rs, rowNum) -> filmMapper(rs)), idFilm).stream().findAny().
                orElseThrow(() -> new NotFoundException("Фильм с id: " + idFilm + " не был найден."));
    }

    @Override
    public void addLikes(Long idFilm, Long userId) {
        jdbcTemplate.update("INSERT INTO LIKES_MOVIE(film_id,user_id) VALUES (?,?)", idFilm, userId);
        jdbcTemplate.update("UPDATE FILM SET rate = rate + 1 WHERE film_id=?", idFilm);
    }

    @Override
    public void deleteLikes(Long idFilm, Long userId) {
        jdbcTemplate.update("DELETE FROM LIKES_MOVIE WHERE film_id=? AND user_id=?", idFilm, userId);
        jdbcTemplate.update("UPDATE FILM SET rate = rate - 1 WHERE film_id=?", idFilm);
    }

    @Override
    public List<FilmGenre> getFilmGenres() {
        return jdbcTemplate.query("SELECT * FROM GENRE", ((rs, rowNum) -> genreMapper(rs)));
    }

    @Override
    public List<Mpa> getMpaRatings() {
        return jdbcTemplate.query("SELECT * FROM MPA", ((rs, rowNum) -> mpaMapper(rs)));
    }

    @Override
    public FilmGenre getFilmGenre(Integer genreId) {
        return jdbcTemplate.query("SELECT * FROM GENRE WHERE genre_id=?",
                        ((rs, rowNum) -> genreMapper(rs)), genreId).
                stream().
                findAny().
                orElseThrow(() -> new NotFoundException("Жанр с id: " + genreId + "не был найден."));
    }

    @Override
    public Mpa getMpaRating(Integer mpaId) {
        return jdbcTemplate.query("SELECT * FROM MPA WHERE mpa_id=?",
                        ((rs, rowNum) -> mpaMapper(rs)), mpaId).
                stream().
                findAny().
                orElseThrow(() -> new NotFoundException("Рейтинг mpa с id: " + mpaId + "не был найден."));
    }

    private Mpa mpaMapper(ResultSet rs) throws SQLException {
        Mpa mpaRating = new Mpa();
        mpaRating.setId(rs.getInt("mpa_id"));
        mpaRating.setName(rs.getString("name"));
        return mpaRating;
    }

    private FilmGenre genreMapper(ResultSet rs) throws SQLException {
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setId(rs.getInt("genre_id"));
        filmGenre.setName(rs.getString("name"));
        return filmGenre;
    }

    private List<FilmGenre> getGenres(Long idFilm) {
        return new ArrayList<>(jdbcTemplate.
                query("SELECT * FROM GENRE WHERE genre_id IN (SELECT genre_id FROM GENRE_FILM WHERE film_id=?) ORDER BY genre_id",
                        ((rs, rowNum) -> genreMapper(rs)), idFilm));
    }

    private Film filmMapper(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setRate(rs.getInt("rate"));
         int mpaId = rs.getInt("mpa_id");
        Mpa mpaRating = getMpaRating(mpaId);
        film.setMpa(mpaRating);
        film.setGenres(getGenres(rs.getLong("film_id")));
        return film;
    }

    private void saveGenre(Film film) {
        jdbcTemplate.update("DELETE GENRE_FILM WHERE film_id=?", film.getId());
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }
        final Set<FilmGenre> genres = new HashSet<>(film.getGenres());
        final ArrayList<FilmGenre> genres1 = new ArrayList<>(genres);
        jdbcTemplate.batchUpdate("INSERT INTO GENRE_FILM(genre_id,film_id) VALUES (?,?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, genres1.get(i).getId());
                        ps.setLong(2, film.getId());
                    }

                    public int getBatchSize() {
                        return genres1.size();
                    }
                });
    }
}

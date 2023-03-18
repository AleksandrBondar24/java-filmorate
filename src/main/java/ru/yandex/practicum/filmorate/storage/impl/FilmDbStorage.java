package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

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
                update("UPDATE FILM SET name=?, description=?, release_date=?, duration=?, rate=?, mpa_id=? " +
                                "WHERE film_id=?",
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
        Map<Long, List<FilmGenre>> filmsGenres = new HashMap<>();
        List<Film> films = jdbcTemplate.query("SELECT *,m.name_mpa " +
                        "FROM FILM f " +
                        "LEFT JOIN MPA m ON f.mpa_id = m.mpa_id",
                ((rs, rowNum) -> filmMapper(rs)));
        if (films.isEmpty()) {
            return films;
        }
        jdbcTemplate.query("SELECT *,gf.film_id " +
                        "FROM GENRE g " +
                        "LEFT JOIN GENRE_FILM gf ON g.genre_id = gf.genre_id",
                ((rs, rowNum) -> assignGenres(rs, films, filmsGenres)));
        return films;
    }

    private List<Film> assignGenres(ResultSet rs, List<Film> films, Map<Long, List<FilmGenre>> filmsGenres) throws SQLException {
        final long filmId = rs.getLong("film_id");
        List<FilmGenre> listGenres = filmsGenres.getOrDefault(filmId, new ArrayList<>());
        listGenres.add(genreMapper(rs));
        filmsGenres.put(filmId, listGenres);
        films.forEach(film -> film.setGenres(filmsGenres.getOrDefault(film.getId(), new ArrayList<>())));
        return films;
    }

    private Film assignGenre(ResultSet rs, Film film, List<FilmGenre> filmGenres) throws SQLException {
        filmGenres.add(genreMapper(rs));
        film.setGenres(filmGenres);
        return film;
    }

    @Override
    public Film getFilm(Long idFilm) {
        List<FilmGenre> filmGenres = new ArrayList<>();
        Film film = jdbcTemplate.query("SELECT *,m.name_mpa " +
                                "FROM FILM f " +
                                "LEFT JOIN MPA m ON f.mpa_id = m.mpa_id " +
                                "WHERE film_id=?",
                        ((rs, rowNum) -> filmMapper(rs)), idFilm).stream().findAny().
                orElseThrow(() -> new NotFoundException("Фильм с id: " + idFilm + " не был найден."));
        jdbcTemplate.query("SELECT *,gf.film_id " +
                        "FROM GENRE g " +
                        "LEFT JOIN GENRE_FILM gf ON g.genre_id = gf.genre_id " +
                        "WHERE film_id=?",
                ((rs, rowNum) -> assignGenre(rs, film, filmGenres)), idFilm);
        return film;
    }

    @Override
    public void addLikes(Long idFilm, Long userId) {
        jdbcTemplate.update("INSERT INTO LIKES_MOVIE(film_id,user_id) VALUES (?,?)", idFilm, userId);
        jdbcTemplate.update("UPDATE FILM SET rate = rate + 1 " +
                "WHERE film_id=?", idFilm);
    }

    @Override
    public void deleteLikes(Long idFilm, Long userId) {
        jdbcTemplate.update("DELETE FROM LIKES_MOVIE " +
                "WHERE film_id=? AND user_id=?", idFilm, userId);
        jdbcTemplate.update("UPDATE FILM SET rate = rate - 1 " +
                "WHERE film_id=?", idFilm);
    }


    @Override
    public List<Film> getListFilmBest(Long count) {
        Map<Long, List<FilmGenre>> filmsGenres = new HashMap<>();
        List<Film> films = jdbcTemplate.query("SELECT *,m.name_mpa " +
                        "FROM FILM f " +
                        "LEFT JOIN MPA m ON f.mpa_id = m.mpa_id " +
                        "ORDER BY rate DESC " +
                        "LIMIT ? ",
                ((rs, rowNum) -> filmMapper(rs)), count);
        if (films.isEmpty()) {
            return films;
        }
        String sql = "SELECT *,gf.film_id " +
                "FROM GENRE g " +
                "LEFT JOIN GENRE_FILM gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id " +
                "IN (" + films.stream().
                map(film -> String.valueOf(film.getId())).collect(Collectors.joining(",")) + ")";
        jdbcTemplate.query(sql, (rs, rowNum) -> assignGenres(rs, films, filmsGenres));
        return films;
    }

    private Mpa mpaMapper(ResultSet rs) throws SQLException {
        Mpa mpaRating = new Mpa();
        mpaRating.setId(rs.getInt("mpa_id"));
        mpaRating.setName(rs.getString("name_mpa"));
        return mpaRating;
    }

    private FilmGenre genreMapper(ResultSet rs) throws SQLException {
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setId(rs.getInt("genre_id"));
        filmGenre.setName(rs.getString("name_genre"));
        return filmGenre;
    }

    private Film filmMapper(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setRate(rs.getInt("rate"));
        Mpa mpaRating = mpaMapper(rs);
        film.setMpa(mpaRating);
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

package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.util.exception.ErrorResponse;
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
        saveDirector(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        int row = jdbcTemplate.
                update("UPDATE FILM SET name=?, description=?, release_date=?, duration=?, mpa_id=? " +
                                "WHERE film_id=?",
                        film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(),
                        film.getId());
        if (row == 0) {
            throw new NotFoundException("Фильм с id: " + film.getId() + "не был найден.");
        }
        saveGenre(film);
        saveDirector(film);
        return getFilm(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        Map<Long, List<FilmGenre>> filmsGenres = new HashMap<>();
        Map<Long, Set<Director>> filmsDirectors = new HashMap<>();
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
        String sql1 = "SELECT *,df.film_id " +
                "FROM directors d " +
                "LEFT JOIN directors_films df ON d.director_id = df.director_id ";
        jdbcTemplate.query(sql1, (rs, rowNum) -> assignDirectors(rs, films, filmsDirectors));
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
        Set<Director> filmDirectors = new HashSet<>();
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
        jdbcTemplate.query("SELECT *,df.film_id " +
                        "FROM directors d " +
                        "LEFT JOIN directors_films df ON d.director_id = df.director_id " +
                        "WHERE film_id=?",
                ((rs, rowNum) -> assignDirector(rs, film, filmDirectors)), idFilm);
        return film;
    }

    @Override
    public void addLikes(Long idFilm, Long userId) {
        Integer likesCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM LIKES_MOVIE " +
                "WHERE film_id = ? AND user_id = ?", Integer.class, idFilm, userId);
        if (likesCount == 1) {
            return;
        }
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
        Map<Long, Set<Director>> filmsDirectors = new HashMap<>();
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
        String sql1 = "SELECT *,df.film_id " +
                "FROM directors d " +
                "LEFT JOIN directors_films df ON d.director_id = df.director_id ";
        jdbcTemplate.query(sql1, (rs, rowNum) -> assignDirectors(rs, films, filmsDirectors));
        return films;
    }

    @Override
    public void deleteFilm(Long filmId) {
        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", filmId);
    }

    private Mpa mpaMapper(ResultSet rs) throws SQLException {
        Mpa mpaRating = new Mpa();
        mpaRating.setId(rs.getLong("mpa_id"));
        mpaRating.setName(rs.getString("name_mpa"));
        return mpaRating;
    }

    private FilmGenre genreMapper(ResultSet rs) throws SQLException {
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setId(rs.getLong("genre_id"));
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
                        ps.setLong(1, genres1.get(i).getId());
                        ps.setLong(2, film.getId());
                    }

                    public int getBatchSize() {
                        return genres1.size();
                    }
                });
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String queryLowerCase = "%" + query.toLowerCase(Locale.ENGLISH) + "%";
        if (by.contains("title") && by.contains("director")) {
            Map<Long, Set<Director>> filmsDirectors = new HashMap<>();
            Map<Long, List<FilmGenre>> filmsGenres = new HashMap<>();
            String sql = "SELECT *, r.name_mpa FROM film f " +
                    "LEFT JOIN MPA r ON f.mpa_id = r.mpa_id " +
                    "LEFT JOIN directors_films df ON f.film_id = df.film_id " +
                    "LEFT JOIN directors d ON d.director_id = df.director_id " +
                    "WHERE LOWER(d.name_director) LIKE ? OR LOWER(name) LIKE ? " +
                    "ORDER BY f.rate DESC";
            List<Film> films = jdbcTemplate.query(sql, ((rs, rowNum) -> filmMapper(rs)), queryLowerCase, queryLowerCase);
            if (films.isEmpty()) {
                return films;
            }
            String sql2 = "SELECT *,df.film_id " +
                    "FROM directors d " +
                    "LEFT JOIN directors_films df ON d.director_id = df.director_id " +
                    "WHERE df.film_id IN (" + films.stream()
                    .map(film -> String.valueOf(film.getId())).collect(Collectors.joining(",")) + ")";
            jdbcTemplate.query(sql2, (rs, rowNum) -> assignDirectors(rs, films, filmsDirectors));
            String sql3 = "SELECT *,gf.film_id " +
                    "FROM GENRE g " +
                    "LEFT JOIN GENRE_FILM gf ON g.genre_id = gf.genre_id " +
                    "WHERE gf.film_id " +
                    "IN (" + films.stream().
                    map(film -> String.valueOf(film.getId())).collect(Collectors.joining(",")) + ")";
            jdbcTemplate.query(sql3, (rs, rowNum) -> assignGenres(rs, films, filmsGenres));
            return films;
        }
        if (by.equals("title")) {
            Map<Long, Set<Director>> filmsDirectors = new HashMap<>();
            Map<Long, List<FilmGenre>> filmsGenres = new HashMap<>();
            String sql = "SELECT *, r.name_mpa FROM film f " +
                    "LEFT JOIN MPA r ON f.mpa_id = r.mpa_id " +
                    "WHERE LOWER(name) LIKE ? " +
                    "ORDER BY f.rate DESC";
            List<Film> films = jdbcTemplate.query(sql, ((rs, rowNum) -> filmMapper(rs)), queryLowerCase);
            if (films.isEmpty()) {
                return films;
            }
            String sql2 = "SELECT *,df.film_id " +
                    "FROM directors d " +
                    "LEFT JOIN directors_films df ON d.director_id = df.director_id " +
                    "WHERE df.film_id IN (" + films.stream()
                    .map(film -> String.valueOf(film.getId())).collect(Collectors.joining(",")) + ")";
            jdbcTemplate.query(sql2, (rs, rowNum) -> assignDirectors(rs, films, filmsDirectors));
            String sql3 = "SELECT *,gf.film_id " +
                    "FROM GENRE g " +
                    "LEFT JOIN GENRE_FILM gf ON g.genre_id = gf.genre_id " +
                    "WHERE gf.film_id " +
                    "IN (" + films.stream().
                    map(film -> String.valueOf(film.getId())).collect(Collectors.joining(",")) + ")";
            jdbcTemplate.query(sql3, (rs, rowNum) -> assignGenres(rs, films, filmsGenres));
            return films;
        }
        Map<Long, Set<Director>> filmsDirectors = new HashMap<>();
        Map<Long, List<FilmGenre>> filmsGenres = new HashMap<>();
        String sql = "SELECT *, r.name_mpa FROM film f " +
                "LEFT JOIN MPA r ON f.mpa_id = r.mpa_id " +
                "LEFT JOIN directors_films df ON f.film_id = df.film_id " +
                "LEFT JOIN directors d ON d.director_id = df.director_id " +
                "WHERE LOWER(d.name_director) LIKE ? " +
                "ORDER BY f.rate DESC";
        List<Film> films = jdbcTemplate.query(sql, ((rs, rowNum) -> filmMapper(rs)), queryLowerCase);
        if (films.isEmpty()) {
            return films;
        }
        String sql2 = "SELECT *,df.film_id " +
                "FROM directors d " +
                "LEFT JOIN directors_films df ON d.director_id = df.director_id " +
                "WHERE df.film_id IN (" + films.stream()
                .map(film -> String.valueOf(film.getId())).collect(Collectors.joining(",")) + ")";
        jdbcTemplate.query(sql2, (rs, rowNum) -> assignDirectors(rs, films, filmsDirectors));
        String sql3 = "SELECT *,gf.film_id " +
                "FROM GENRE g " +
                "LEFT JOIN GENRE_FILM gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id " +
                "IN (" + films.stream().
                map(film -> String.valueOf(film.getId())).collect(Collectors.joining(",")) + ")";
        jdbcTemplate.query(sql3, (rs, rowNum) -> assignGenres(rs, films, filmsGenres));
        return films;
    }

    @Override
    public List<Film> getFilmByDirectorByYear(Director director, String sortBy) {
        Map<Long, List<FilmGenre>> filmsGenres = new HashMap<>();
        List<Film> films = jdbcTemplate.query("SELECT *, r.name_mpa " +
                        "FROM film f " +
                        "LEFT JOIN MPA r ON f.mpa_id = r.mpa_id " +
                        "LEFT JOIN directors_films df ON f.film_id = df.film_id " +
                        "WHERE df.director_id = ? " +
                        "ORDER BY f.release_date",
                ((rs, rowNum) -> filmMapper(rs)), director.getId());
        if (films.isEmpty()) {
            return films;
        }
        String sql3 = "SELECT *,gf.film_id " +
                "FROM GENRE g " +
                "LEFT JOIN GENRE_FILM gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id " +
                "IN (" + films.stream().
                map(film -> String.valueOf(film.getId())).collect(Collectors.joining(",")) + ")";
        jdbcTemplate.query(sql3, (rs, rowNum) -> assignGenres(rs, films, filmsGenres));
        Set<Director> directors = new HashSet<>();
        directors.add(director);
        films.forEach(film -> film.setDirectors(directors));
        return films;
    }

    @Override
    public List<Film> getFilmByDirectorByLikes(Director director, String sortBy) {
        Map<Long, List<FilmGenre>> filmsGenres = new HashMap<>();
        List<Film> films = jdbcTemplate.query("SELECT *, r.name_mpa " +
                        "FROM film f " +
                        "LEFT JOIN MPA r ON f.mpa_id = r.mpa_id " +
                        "LEFT JOIN directors_films df ON f.film_id = df.film_id " +
                        "WHERE df.director_id = ? " +
                        "ORDER BY f.rate DESC",
                ((rs, rowNum) -> filmMapper(rs)), director.getId());
        if (films.isEmpty()) {
            return films;
        }
        String sql3 = "SELECT *,gf.film_id " +
                "FROM GENRE g " +
                "LEFT JOIN GENRE_FILM gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id " +
                "IN (" + films.stream().
                map(film -> String.valueOf(film.getId())).collect(Collectors.joining(",")) + ")";
        jdbcTemplate.query(sql3, (rs, rowNum) -> assignGenres(rs, films, filmsGenres));
        Set<Director> directors = new HashSet<>();
        directors.add(director);
        films.forEach(film -> film.setDirectors(directors));
        return films;
    }

    @Override
    public List<Film> assignDirectors(ResultSet rs, List<Film> films, Map<Long, Set<Director>> filmsDirectors) throws SQLException {
        final Long filmId;
        filmId = rs.getLong("film_id");
        Set<Director> setDirectors = filmsDirectors.getOrDefault(filmId, new HashSet<>());
        setDirectors.add(directorMapper(rs));
        filmsDirectors.put(filmId, setDirectors);
        films.forEach(film -> film.setDirectors(filmsDirectors.getOrDefault(film.getId(), new HashSet<>())));
        return films;
    }

    private Film assignDirector(ResultSet rs, Film film, Set<Director> filmDirectors) throws SQLException {
        filmDirectors.add(directorMapper(rs));
        film.setDirectors(filmDirectors);
        return film;
    }

    private Director directorMapper(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getLong("director_id"));
        director.setName(rs.getString("name_director"));
        return director;
    }

    private void saveDirector(Film film) {
        jdbcTemplate.update("DELETE directors_films WHERE film_id=?", film.getId());
        if (film.getDirectors() == null || film.getDirectors().isEmpty()) {
            return;
        }
        final Set<Director> directors = new HashSet<>(film.getDirectors());
        final ArrayList<Director> directors1 = new ArrayList<>(directors);
        jdbcTemplate.batchUpdate("INSERT INTO directors_films(director_id,film_id) VALUES (?,?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setLong(1, directors1.get(i).getId());
                        ps.setLong(2, film.getId());
                    }

                    public int getBatchSize() {
                        return directors1.size();
                    }
                });
    }

    @Override
    public List<Film> getRecommendation(Long id) {
        Map<Long, List<FilmGenre>> filmsGenres = new HashMap<>();
        String filmsId = "SELECT film_id FROM LIKES_MOVIE " +
                "WHERE user_id = ?";
        List<Long> usersFilms = new ArrayList<>();
        jdbcTemplate.query(filmsId, (rs, rowNum) -> mapFilmsId(rs, usersFilms), id);
        String recommendUserId = "SELECT user_id FROM LIKES_MOVIE " +
                "GROUP BY user_id,film_id " +
                "HAVING film_id IN (" + usersFilms.stream().map(String::valueOf).collect(Collectors.joining(",")) + ") AND user_id != ? " +
                "ORDER BY COUNT(film_id) desc " +
                "LIMIT 1";
        List<Long> usersId = new ArrayList<>();
        jdbcTemplate.query(recommendUserId, (rs, rowNum) -> mapUsersId(rs, usersId), id);

        String recommendations = "SELECT f.*, r.name_mpa " +
                "FROM film  f " +
                "LEFT JOIN MPA r ON f.mpa_id = r.mpa_id " +
                "LEFT JOIN LIKES_MOVIE likes ON f.film_id = likes.film_id " +
                "WHERE likes.user_id IN (" + usersId.stream().map(String::valueOf).collect(Collectors.joining(",")) + ") " +
                "AND f.film_id NOT IN (" + usersFilms.stream().map(String::valueOf).collect(Collectors.joining(",")) + ") ";

        Map<Long, Set<Director>> filmsDirectors = new HashMap<>();
        List<Film> films = jdbcTemplate.query(recommendations, ((rs, rowNum) -> filmMapper(rs)));
        if (films.isEmpty()) {
            return films;
        }
        String sql2 = "SELECT *,gf.film_id " +
                "FROM GENRE g " +
                "LEFT JOIN GENRE_FILM gf ON g.genre_id = gf.genre_id " +
                "WHERE gf.film_id " +
                "IN (" + films.stream().
                map(film -> String.valueOf(film.getId())).collect(Collectors.joining(",")) + ")";
        jdbcTemplate.query(sql2, (rs, rowNum) -> assignGenres(rs, films, filmsGenres));
        String sql3 = "SELECT *,df.film_id " +
                "FROM DIRECTORS d " +
                "LEFT JOIN DIRECTORS_FILMS df ON d.director_id = df.director_id ";
        jdbcTemplate.query(sql3, (rs, rowNum) -> assignDirectors(rs, films, filmsDirectors));
        return films;
    }

    private List<Long> mapFilmsId(ResultSet resultSet, List<Long> usersFilms) throws SQLException {
        usersFilms.add(resultSet.getLong("film_id"));
        return usersFilms;
    }

    private List<Long> mapUsersId(ResultSet resultSet, List<Long> usersId) throws SQLException {
        usersId.add(resultSet.getLong("user_id"));
        return usersId;
    }
}

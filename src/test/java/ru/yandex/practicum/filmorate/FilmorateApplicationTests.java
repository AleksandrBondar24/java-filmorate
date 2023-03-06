package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private User user;

    @BeforeEach
    public void createUser() {
        user = new User();
        user.setEmail("abs@mail.ru");
        user.setLogin("abs");
        user.setName("sba");
        user.setBirthday(LocalDate.of(1999, 10, 12));
    }

    @Test
    public void testUserDbStorage() {
        long userId = userStorage.save(user);
        Assertions.assertEquals(userId, 1L);

        User user1 = userStorage.getUser(userId);
        assertThat(user1).hasFieldOrPropertyWithValue("id", 1L);

        user1.setName("bos");
        User user2 = userStorage.update(user1);
        assertThat(user2).hasFieldOrPropertyWithValue("name", "bos");

        User friend = new User();
        friend.setEmail("sss@mail.ru");
        friend.setLogin("sss");
        friend.setName("ttt");
        friend.setBirthday(LocalDate.of(1900, 8, 10));

        long friendId = userStorage.save(friend);
        userStorage.addFriend(userId, friendId);
        Set<User> friends = userStorage.getListFriends(userId);
        Assertions.assertEquals(1, friends.size());

        userStorage.deleteFriend(userId, friendId);
        Set<User> friends1 = userStorage.getListFriends(userId);
        Assertions.assertEquals(0, friends1.size());

        List<User> users = userStorage.getUsers();
        Assertions.assertEquals(2, users.size());
    }

    @Test
    public void testFilmDbStorage() {
        Film film = new Film();
        film.setName("dzy");
        film.setDescription("blabla");
        film.setReleaseDate(LocalDate.of(1967, 12, 2));
        film.setDuration(67);
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);
        FilmGenre filmGenre = new FilmGenre();
        filmGenre.setId(1);
        List<FilmGenre> filmGenres = List.of(filmGenre);
        film.setGenres(filmGenres);

        long filmId = filmDbStorage.save(film);
        Assertions.assertEquals(filmId, 1L);

        long userId = userStorage.save(user);
        filmDbStorage.addLikes(filmId, userId);

        Film film1 = filmDbStorage.getFilm(1L);
        assertThat(film1).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(film1).hasFieldOrPropertyWithValue("rate", 1);

        filmDbStorage.deleteLikes(filmId, userId);
        Film film2 = filmDbStorage.getFilm(1L);
        assertThat(film2).hasFieldOrPropertyWithValue("rate", 0);

        film1.setDuration(77);
        Film film3 = filmDbStorage.update(film1);
        assertThat(film3).hasFieldOrPropertyWithValue("duration", 77);

        List<Film> films = filmDbStorage.getFilms();
        Assertions.assertEquals(1, films.size());

        List<FilmGenre> filmGenres1 = filmDbStorage.getFilmGenres();
        Assertions.assertEquals(6, filmGenres1.size());

        FilmGenre filmGenre1 = filmDbStorage.getFilmGenre(2);
        assertThat(filmGenre1).hasFieldOrPropertyWithValue("name", "Драма");

        List<Mpa> mpaRatings = filmDbStorage.getMpaRatings();
        Assertions.assertEquals(5, mpaRatings.size());

        Mpa mpa1 = filmDbStorage.getMpaRating(3);
        assertThat(mpa1).hasFieldOrPropertyWithValue("name", "PG-13");
    }
}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.service.FilmService.data;


public class FilmControllerTest {

    private FilmController controller;
    private Film film;
    private FilmStorage storage;
    private UserStorage storageUser;
    private FilmService service;

    @BeforeEach
    public void createFilmAndController() {
        storage = new InMemoryFilmStorage();
        storageUser = new InMemoryUserStorage();
        service = new FilmService(storage, storageUser);
        controller = new FilmController(service);
        film = new Film();
        film.setName("Wither");
        film.setDescription("fantasy");
        LocalDate localDate = LocalDate.of(2019, 12, 20);
        film.setReleaseDate(localDate);
        film.setDuration(45);
    }

    @Test
    public void shouldValidateFilmOk() {
        service.validate(film);
        film.setReleaseDate(data);
        service.validate(film);
    }

    @Test
    public void shouldValidateFilmFail() {
        LocalDate localDate = LocalDate.of(1800, 12, 20);
        film.setReleaseDate(localDate);
        Exception exception = assertThrows(ValidationException.class, ()->service.validate(film));

        Assertions.assertEquals("Дата релиза фильма не должна быть раньше 1985.12.28", exception.getMessage());
    }
 }



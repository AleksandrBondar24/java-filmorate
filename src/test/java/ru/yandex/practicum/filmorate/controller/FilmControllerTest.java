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
import static ru.yandex.practicum.filmorate.controller.FilmController.data;


public class FilmControllerTest {

    private FilmController controller;
    private Film film;

    @BeforeEach
    public void createFilmAndController() {
        FilmStorage storage = new InMemoryFilmStorage();
        UserStorage storageUser = new InMemoryUserStorage();
        FilmService service = new FilmService(storage, storageUser);
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
        controller.validate(film);
        film.setReleaseDate(data);
        controller.validate(film);
    }

    @Test
    public void shouldValidateFilmFail() {
        LocalDate localDate = LocalDate.of(1800, 12, 20);
        film.setReleaseDate(localDate);
        Exception exception = assertThrows(ValidationException.class, ()->controller.validate(film));

        Assertions.assertEquals("Ошибка валидации", exception.getMessage());
    }
 }



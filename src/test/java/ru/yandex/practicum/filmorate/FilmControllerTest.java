package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.LocalDate;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.controller.FilmController.data;

public class FilmControllerTest {

    private FilmController controller;
    private Film film;

    @BeforeEach
    public void createFilmAndController() {
        controller = new FilmController();
        film = new Film();
        film.setName("Wither");
        film.setDescription("fantasy");
        LocalDate localDate = LocalDate.of(2019, 12, 20);
        film.setReleaseDate(localDate);
        film.setDuration(45);
    }

    @Test
    public void shouldCheckIdFilm() {
        ResponseEntity<Film> response = controller.createFilm(film);
        int id = Objects.requireNonNull(response.getBody()).getId();

        Assertions.assertEquals(1, id);

        ResponseEntity<Film> response1 = controller.updateFilm(film);
        int status = response1.getStatusCode().value();

        Assertions.assertEquals(200, status);

        film.setId(999);
        ResponseEntity<Film> response2 = controller.updateFilm(film);
        int status1 = response2.getStatusCode().value();

        Assertions.assertEquals(404, status1);

        film.setId(0);
        ResponseEntity<Film> response3 = controller.updateFilm(film);
        int status2 = response3.getStatusCode().value();

        Assertions.assertEquals(404, status2);
    }

    @Test
    public void shouldCheckReleaseDateValidation() {
        ResponseEntity<Film> response = controller.createFilm(film);
        int status = response.getStatusCode().value();

        Assertions.assertEquals(200, status);

        LocalDate localDate1 = LocalDate.of(1800, 12, 20);
        film.setReleaseDate(localDate1);
        ResponseEntity<Film> response1 = controller.createFilm(film);
        int status1 = response1.getStatusCode().value();

        Assertions.assertEquals(400, status1);

        film.setReleaseDate(data);
        ResponseEntity<Film> response2 = controller.createFilm(film);
        int status2 = response2.getStatusCode().value();

        Assertions.assertEquals(400, status2);

        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Wither");
        film1.setDescription("fantasy");
        LocalDate localDate = LocalDate.of(2020, 12, 20);
        film1.setReleaseDate(localDate);
        film1.setDuration(45);
        ResponseEntity<Film> response3 = controller.updateFilm(film1);
        int status3 = response3.getStatusCode().value();

        Assertions.assertEquals(200, status3);

        Film film2 = new Film();
        film2.setId(1);
        film2.setName("Wither");
        film2.setDescription("fantasy");
        film2.setReleaseDate(data);
        film2.setDuration(45);
        ResponseEntity<Film> response4 = controller.updateFilm(film2);
        int status4 = response4.getStatusCode().value();

        Assertions.assertEquals(404, status4);

        Film film3 = new Film();
        film3.setId(1);
        film3.setName("Wither");
        film3.setDescription("fantasy");
        LocalDate localDate2 = LocalDate.of(1801, 12, 20);
        film3.setReleaseDate(localDate2);
        film3.setDuration(45);
        ResponseEntity<Film> response5 = controller.updateFilm(film3);
        int status5 = response5.getStatusCode().value();

        Assertions.assertEquals(404, status5);
    }
}


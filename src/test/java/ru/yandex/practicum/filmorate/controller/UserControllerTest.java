package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

public class UserControllerTest {
    private UserController controller;
    private UserStorage storage;
    private UserService service;
    private User user;
    @BeforeEach
    public void createUserAndController() {
        storage = new InMemoryUserStorage();
        service = new UserService(storage);
        controller = new UserController(service);
        user = new User();
        user.setEmail("alex@yandex.ru");
        user.setLogin("beglets");
        user.setName("Aleksandr");
        LocalDate localDate = LocalDate.of(1985, 4, 24);
        user.setBirthday(localDate);
    }

    @Test
    public void shouldValidateUserOk() {
        service.validate(user);

        Assertions.assertEquals("Aleksandr",user.getName());
    }

    @Test
    public void shouldValidateUserNameNull() {
        user.setName(null);
        service.validate(user);

        Assertions.assertEquals("beglets", user.getName());
    }
    @Test
    public void shouldValidateUserNameBlank() {
        user.setName(" ");
        service.validate(user);

        Assertions.assertEquals("beglets", user.getName());
    }
}


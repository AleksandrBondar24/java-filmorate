package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.util.Objects;

public class UserControllerTest {
    private UserController controller;
    private User user;

    @BeforeEach
    public void createUserAndController() {
        controller = new UserController();
        user = new User();
        user.setEmail("alex@yandex.ru");
        user.setLogin("beglets");
        user.setName("Aleksandr");
        LocalDate localDate = LocalDate.of(1985, 4, 24);
        user.setBirthday(localDate);
    }

    @Test
    public void shouldCheckIdUser() {
        ResponseEntity<User> response = controller.createUser(user);
        Long id = Objects.requireNonNull(response.getBody()).getId();

        Assertions.assertEquals(1, id);

        ResponseEntity<User> response1 = controller.updateUser(user);
        int status = response1.getStatusCode().value();

        Assertions.assertEquals(200, status);

        user.setId(999L);
        ResponseEntity<User> response2 = controller.updateUser(user);
        int status1 = response2.getStatusCode().value();

        Assertions.assertEquals(404, status1);

        user.setId(0L);
        ResponseEntity<User> response3 = controller.updateUser(user);
        int status2 = response3.getStatusCode().value();

        Assertions.assertEquals(404, status2);
    }

    @Test
    public void shouldCheckUsernameValidation() {
        ResponseEntity<User> response = controller.createUser(user);
        String name = Objects.requireNonNull(response.getBody()).getName();

        Assertions.assertEquals("Aleksandr", name);

        User user2 = new User();
        user2.setEmail("alex@yandex.ru");
        user2.setLogin("beglets");
        LocalDate localDate = LocalDate.of(1985, 4, 24);
        user2.setBirthday(localDate);
        ResponseEntity<User> response1 = controller.createUser(user2);
        String name1 = Objects.requireNonNull(response1.getBody()).getName();

        Assertions.assertEquals("beglets", name1);

        User user3 = new User();
        user3.setId(2L);
        user3.setEmail("alex@yandex.ru");
        user3.setLogin("beglets");
        LocalDate localDate1 = LocalDate.of(1985, 4, 24);
        user3.setBirthday(localDate1);
        ResponseEntity<User> response2 = controller.updateUser(user3);
        String name2 = Objects.requireNonNull(response2.getBody()).getName();

        Assertions.assertEquals("beglets", name2);
    }
}


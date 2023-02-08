package ru.yandex.practicum.filmorate.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
public class UserErrorResponse {
    private String message;
    private LocalDateTime timestamp;
}

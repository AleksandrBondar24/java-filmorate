package ru.yandex.practicum.filmorate.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Setter
@Getter
public class ErrorResponse {
    private String message;
    private long timestamp;
}

package ru.yandex.practicum.filmorate.util.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;


@AllArgsConstructor
@Setter
@Getter
public class ErrorResponse extends RuntimeException {
    private String message;
    private long timestamp;
}

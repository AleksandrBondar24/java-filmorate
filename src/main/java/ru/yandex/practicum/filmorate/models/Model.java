package ru.yandex.practicum.filmorate.models;

import lombok.Getter;
import lombok.Setter;

public abstract class Model {
    @Setter
    @Getter
    protected Long id;
}

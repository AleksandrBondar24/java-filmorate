package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class Model {
    @NonNull
    protected Long id;
}

package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@EqualsAndHashCode
public abstract class Model {
    @NonNull
    protected Long id;
}

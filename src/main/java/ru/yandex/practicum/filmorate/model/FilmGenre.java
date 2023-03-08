package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FilmGenre {
    int id;
    String name;
}

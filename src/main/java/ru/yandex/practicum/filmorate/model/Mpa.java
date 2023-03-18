package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Mpa {
    private int id;
    private String name;
}

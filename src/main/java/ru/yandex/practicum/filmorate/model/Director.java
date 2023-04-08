package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)
public class Director extends Model {
    @NotBlank
    private String name;

    public Map<String, String> toMap() {
        Map<String, String> values = new HashMap<>();
        values.put("name_director", name);
        return values;
    }
}

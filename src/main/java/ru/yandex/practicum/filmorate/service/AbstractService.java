package ru.yandex.practicum.filmorate.service;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.yandex.practicum.filmorate.model.Model;
import ru.yandex.practicum.filmorate.util.exception.NotFoundException;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.List;

public abstract class AbstractService<T extends Model> {

    public T save(T o, BindingResult result) {
        errorMessageBuilder(result);
        validate(o);
        return o;
    }

    public T update(T o) {
        validate(o);
        return o;
    }

    protected void add(Long id, Long otherId) {
        validateIds(id, otherId);
    }

    protected void delete(Long id, Long otherId) {
        validateIds(id, otherId);
    }

    protected abstract void validate(T o);

    protected void validateIds(Long id, Long otherId) {
        if (id <= 0 || otherId <= 0) {
            throw new NotFoundException("Идентификатор должен быть положительным числом.");
        }
    }

    protected void validateId(Long id) {
        if (id <= 0) {
            throw new NotFoundException("Идентификатор должен быть положительным числом.");
        }
    }

    private void errorMessageBuilder(BindingResult result) throws ValidationException {
        if (result.hasErrors()) {
            List<FieldError> errors = result.getFieldErrors();
            StringBuilder errorMs = new StringBuilder();
            for (FieldError error : errors) {
                errorMs.append(error.getField()).
                        append("-").append(error.getDefaultMessage()).
                        append(";");
            }
            throw new ValidationException(errorMs.toString());
        }
    }
}

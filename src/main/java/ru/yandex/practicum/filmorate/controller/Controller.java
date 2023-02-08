package ru.yandex.practicum.filmorate.controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import ru.yandex.practicum.filmorate.model.Model;;
import ru.yandex.practicum.filmorate.util.exception.ValidationException;

import java.util.List;

public abstract class Controller<T extends Model> {
    private Long generatorId = 1L;

    public T create(T o, BindingResult result) {
        errorMessageBuilder(result);
        validate(o);
        o.setId(generatorId);
        generatorId++;
        return o;
    }

    public T update(T o) {
        validate(o);
        return o;
    }
    protected abstract void add(Long id,Long id1);
    protected abstract void delete(Long id,Long id1);
    protected abstract List<T> getList(Long id);

    protected abstract void validate(T o);
    protected abstract List<T> getListModels();

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

package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public enum SortDirectorFilms {
    LIKES("likes"),
    YEAR("year");

    private final String textView;

    SortDirectorFilms(String textView) {
        this.textView = textView;
    }

    public static SortDirectorFilms getSortByName(String sortName) {
        if (!StringUtils.hasText(sortName)) {
            throw new IllegalStateException("Значение сортировки не должно быть пустым.");
        }

        try {
            return SortDirectorFilms.valueOf(sortName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Сортировки '%s' не существует.".formatted(sortName));
        }
    }
}